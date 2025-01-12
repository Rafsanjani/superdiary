require 'json'

COMPOSE_RULES_VERSION = '0.4.22'
KTLINT_CLI_VERSION = '1.5.0'

module Danger
  class DangerKtlint < Plugin
    class UnexpectedLimitTypeError < StandardError; end

    class UnsupportedServiceError < StandardError
      def initialize(message = 'Unsupported service! Currently supported services are GitHub, GitLab and BitBucket server.')
        super(message)
      end
    end

    AVAILABLE_SERVICES = [:github, :gitlab, :bitbucket_server]

    # TODO: Lint all files if `filtering: false`
    attr_accessor :filtering

    attr_accessor :skip_lint, :report_file, :report_files_pattern

    def limit
      @limit ||= nil
    end

    def limit=(limit)
      if limit != nil && limit.integer?
        @limit = limit
      else
        raise UnexpectedLimitTypeError
      end
    end

   # Run ktlint using the CLI. This plugin will go ahead and
   # install all the necessary dependencies before running the actual checks
    def lint(inline_mode: false)
      unless supported_service?
        raise UnsupportedServiceError.new
      end

      targets = target_files(git.added_files + git.modified_files)

      results = ktlint_results(targets)
      if results.nil? || results.empty?
        return
      end

      if inline_mode
        send_inline_comments(results, targets)
      else
        send_markdown_comment(results, targets)
      end

      cleanup()
    end

    def send_markdown_comment(ktlint_results, targets)
      catch(:loop_break) do
        count = 0
        ktlint_results.each do |ktlint_result|
          ktlint_result.each do |result|
            result['errors'].each do |error|
              file_path = relative_file_path(result['file'])
              next unless targets.include?(file_path)

              message = "#{file_html_link(file_path, error['line'])}: #{error['message']}"
              fail(message)
              unless limit.nil?
                count += 1
                if count >= limit
                  throw(:loop_break)
                end
              end
            end
          end
        end
      end
    end

    def send_inline_comments(ktlint_results, targets)
      catch(:loop_break) do
        count = 0
        ktlint_results.each do |ktlint_result|
          ktlint_result.each do |result|
            result['errors'].each do |error|
              file_path = relative_file_path(result['file'])
              next unless targets.include?(file_path)
              message = error['message']
              line = error['line']
              fail(message, file: result['file'], line: line)
              unless limit.nil?
                count += 1
                if count >= limit
                  throw(:loop_break)
                end
              end
            end
          end
        end
      end
    end

    def target_files(changed_files)
      changed_files.select do |file|
        file.end_with?('.kt')
      end
    end

    # Make it a relative path so it can compare it to git.added_files
    def relative_file_path(file_path)
      file_path.gsub(/#{pwd}\//, '')
    end

    private

    def file_html_link(file_path, line_number)
      file = if danger.scm_provider == :github
               "#{file_path}#L#{line_number}"
             else
               file_path
             end
      scm_provider_klass.html_link(file)
    end

    # `eval` may be dangerous, but it does not accept any input because it accepts only defined as danger.scm_provider
    def scm_provider_klass
      @scm_provider_klass ||= eval(danger.scm_provider.to_s)
    end

    def pwd
      @pwd ||= `pwd`.chomp
    end

    def ktlint_exists?
      system 'which ktlint > /dev/null 2>&1'
    end

    def ktlint_results(targets)
      if skip_lint
        ktlint_result_files.map do |file|
          File.open(file) do |f|
            JSON.load(f)
          end
        end
      else
        download_dependencies()
        unless ktlint_exists?
          fail("Couldn't find ktlint command. Something must have gone wrong during the installation")
          return
        end
        return if targets.empty?

        [JSON.parse(`ktlint --ruleset=compose-rules.jar #{targets.join(' ')} --reporter=json --relative --log-level=none`)]
      end
    end

  def cleanup()
      `rm -f compose-rules.jar`
  end

   def download_dependencies
       #Download ktlint CLI tool and move it to bin directory
       `curl -sSLO https://github.com/pinterest/ktlint/releases/download/#{KTLINT_CLI_VERSION}/ktlint && chmod a+x ktlint && sudo mv ktlint /usr/local/bin/`

       # Download compose-rules.jar
       `curl -L https://github.com/mrmans0n/compose-rules/releases/download/v#{COMPOSE_RULES_VERSION}/ktlint-compose-#{COMPOSE_RULES_VERSION}-all.jar -o compose-rules.jar`
   end

    def supported_service?
      AVAILABLE_SERVICES.include?(danger.scm_provider.to_sym)
    end

    def ktlint_result_files
      if !report_file.nil? && !report_file.empty? && File.exist?(report_file)
        [report_file]
      elsif !report_files_pattern.nil? && !report_files_pattern.empty?
        Dir.glob(report_files_pattern)
      else
        fail("Couldn't find ktlint result json file.\nYou must specify it with `ktlint.report_file=...` or `ktlint.report_files_pattern=...` in your Dangerfile.")
      end
    end
  end
end
