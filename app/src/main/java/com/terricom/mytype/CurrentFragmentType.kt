package com.terricom.mytype

enum class CurrentFragmentType(val value: String) {
    FOODIE(App.instance?.getString(R.string.title_foodie) as String),
    DIARY(App.instance?.getString(R.string.title_diary) as String),
    LINECHART(App.instance?.getString(R.string.title_linechart) as String),
    HARVEST(App.instance?.getString(R.string.title_harvest) as String),
    LOGIN(""),
    DREAMBOARD(""),
    DREAMBOARD_REMINDER("")
}