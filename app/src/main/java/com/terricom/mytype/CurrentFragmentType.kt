package com.terricom.mytype

enum class CurrentFragmentType(val value: String) {
    FOODIE(App.instance?.getString(R.string.title_foodie) as String),
    DIARY(App.instance?.getString(R.string.title_diary) as String),
    LINE_CHART(App.instance?.getString(R.string.title_linechart) as String),
    ACHIEVEMENT(App.instance?.getString(R.string.title_achievement) as String),
    LOGIN(""),
    DREAM_BOARD(App.instance?.getString(R.string.title_dream_puzzle) as String),
    SHAPE_RECORD(App.instance?.getString(R.string.title_shape_record) as String),
    PROFILE(App.instance?.getString(R.string.title_profile) as String),
    SLEEP(App.instance?.getString(R.string.title_sleep) as String),
    GOAL(App.instance?.getString(R.string.title_goal_setting) as String)
}