// Copyright 2023, Google LLC, Christopher Banes and the Tivi project contributors
// SPDX-License-Identifier: Apache-2.0

@file:Suppress("TooManyFunctions", "ReturnCount")

package com.foreverrafs.benchmark

import android.os.SystemClock
import androidx.test.uiautomator.By
import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.SearchCondition
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

object AppScenarios {
    fun mainNavigationItems(device: UiDevice) {
        device.waitForIdle()

        // -------------
        // Dashboard
        // -------------
        device.testDashboard() || return

        // -------------
        // Favorites
        // -------------
        device.testFavorites() || return
    }
}

private fun UiDevice.testDashboard(): Boolean {
    // There should be no entry in the list so click on the 'Add entry' button

    addEntryFromDashboard()

    waitForIdle()

    return true
}

private fun UiDevice.addEntryFromDashboard() {
    waitForObject(By.res("dashboard_content_list"), 30.seconds)

    runAction(By.res("button_add_entry")) {
        click()
    }

    runAction(By.res("diary_text_field")) {
        text = "Hello Diary, I miss you"
    }

    runAction(By.res("button_save_diary")) {
        click()
    }
}

private fun UiDevice.testFavorites(): Boolean {
    waitForObject(By.res("empty_favorite_text"), 30.seconds)

    runAction(By.res("Favorites")) {
        click()
    }

    addEntryFromDashboard()
    addFavoriteFromDashboard()
//    navigateToFavorites()

    waitForIdle()

    return true
}

private fun UiDevice.addFavoriteFromDashboard() {
    waitForIdle()
    runAction(By.res("diary_list_item_1")) {
        swipe(Direction.LEFT, 0.8f)
    }
}

fun UiDevice.waitForObject(selector: BySelector, timeout: Duration = 5.seconds): UiObject2 {
    if (wait(Until.hasObject(selector), timeout)) {
        return findObject(selector)
    }
    error("Object with selector [$selector] not found")
}

fun <R> UiDevice.wait(condition: SearchCondition<R>, timeout: Duration): R = wait(condition, timeout.inWholeMilliseconds)

private fun UiDevice.runAction(
    selector: BySelector,
    maxRetries: Int = 6,
    action: UiObject2.() -> Unit,
) {
    waitForObject(selector)

    retry(maxRetries = maxRetries, delay = 1.seconds) {
        // Wait for idle, to avoid recompositions causing StaleObjectExceptions
        waitForIdle()

        requireNotNull(findObject(selector)).action()
    }
}

private fun retry(maxRetries: Int, delay: Duration, block: () -> Unit) {
    repeat(maxRetries) { run ->
        val result = runCatching { block() }
        if (result.isSuccess) {
            return
        }
        if (run == maxRetries - 1) {
            result.getOrThrow()
        } else {
            SystemClock.sleep(delay.inWholeMilliseconds)
        }
    }
}
