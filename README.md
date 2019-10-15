# MyType 種天菜
幫助使用者建立檢視飲食的習慣，達成體內平衡的工具

[<img width="200" height="90" src="https://firebasestorage.googleapis.com/v0/b/mytype-201909.appspot.com/o/images%2Fprojects%2Fgoogle-play-badge.png?alt=media&token=8ea1c5a5-3bd5-493e-a809-f555805259b4"/>](https://play.google.com/store/apps/details?id=com.terricom.mytype)

測試用 Google 帳號：
(帳號) mytype520@gmail.com
(密碼) ChiaCai99

功能介紹
---
1. 日記功能：幫使用者快速回顧今日菜單、體態成果和睡眠時間
<div align="left">
<img width="270" height="480" src="https://firebasestorage.googleapis.com/v0/b/mytype-201909.appspot.com/o/images%2Fprojects%2FScreenshot_diary_with_sleep_and_shape.png?alt=media&token=bc82d3f5-c342-4722-9d45-de83853cebf3">
<img width="270" height="480" src="https://firebasestorage.googleapis.com/v0/b/mytype-201909.appspot.com/o/images%2Fprojects%2FScreenshot_diary.png?alt=media&token=5cc4c0bc-af35-4192-84d1-e34c62749ad8">
</div>


2. 食記功能：使用者可以透過拍照或是選擇相簿內的照片，並且紀錄食物和營養素

<img width="270" height="480" src="https://firebasestorage.googleapis.com/v0/b/mytype-201909.appspot.com/o/images%2Fprojects%2FScreenshot_foodie.png?alt=media&token=ef19c9e8-b05a-41ff-a94b-981da107354f"/>


3. 日曆功能：顯示歷史紀錄，幫助使用者回顧過去的內容進而修正

<img width="270" height="480" src="https://firebasestorage.googleapis.com/v0/b/mytype-201909.appspot.com/o/images%2Fprojects%2FScreenshot_calendar.png?alt=media&token=ca6d3901-2729-45e8-bd55-f1ea8c864142"/>


4. 目標功能：系統會根據使用者設定的每日飲食目標發送推播通知，提醒使用者尚缺多少營養

<div align="left">
<img width="270" height="480" src="https://firebasestorage.googleapis.com/v0/b/mytype-201909.appspot.com/o/images%2Fprojects%2FScreenshot_profile.png?alt=media&token=d862653f-7756-490e-8ab0-edf60307a81e">
<img width="270" height="480" src="https://firebasestorage.googleapis.com/v0/b/mytype-201909.appspot.com/o/images%2Fprojects%2FScreenshot_notification.png?alt=media&token=9c46b443-190a-4e03-b2fc-c8bfa85e423f">
</div>


5. 查詢功能：使用者可以針對個別食物查詢歷史紀錄，並了解該項食物對於目標達成的單位貢獻

<img width="270" height="480" src="https://firebasestorage.googleapis.com/v0/b/mytype-201909.appspot.com/o/images%2Fprojects%2FScreenshot_query.png?alt=media&token=d9c29f83-264f-47d1-905c-a03266708df5"/>


6. 圖表功能：透果折線圖幫助使用者了解體態成果與飲食紀錄的相關性

<img width="270" height="480" src="https://firebasestorage.googleapis.com/v0/b/mytype-201909.appspot.com/o/images%2Fprojects%2FScreenshot_line_chart.png?alt=media&token=100c2bf0-9aca-4dcd-a60a-71d1886ac698"/>

框架與工具
---
1. 設計模式：MVVM、Singleton
2. 實作功能：Alarm Receiver (本地端定時推播)、RecyclerView (實作客製化日曆)、Canvas (實作客製化折線圖)、登入 (Facebook SDK、Firebase Authorization)
3. Jetpack：ViewModel、LiveData、Lifecycle、Data Binding、Navigation
4. 分析工具：Firebase Crashlytics、Firebase Ayalytics
5. 測試工具：JUnit、Mockito、Espresso

開發環境
---
* Android Studio 3.5+
* Android SDK 26+
* Gradle 5.1.1+

版本更新
---
* 1.1.2：2019/10/15

聯絡資訊
---
Terri Yang
mo0922@gmail.com
