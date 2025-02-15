package com.foreverrafs.superdiary.list.di

import com.foreverrafs.superdiary.list.presentation.DiaryListViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val diaryListModule = module {
    viewModelOf(::DiaryListViewModel)
}
