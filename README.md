## Secure User Access Session
The AndroidX Security library provides Encrypted SharePreferences which can assist developers implement user sessions that provide seamless user experience without compromising on application security. Using encrypted SharePreferences, the session data such as access tokens, keys, passwords and PII data is stored in encrypted key-value pairs within the user device and can be retrieved any time when its required from the path, `data/data/<YOUR_APP_ID>/shared_prefs/<SHARED_PREF_NAME>.xml`

## Implementation
This repository contains source code for a simple application to illustrate the implementation of encrypted SharedPreferences in securing user session data. The application allows a user to register (1), login (2) and view their profile data as retrieved from the stored data in SharedPreferences (3).

![Application Flow](https://bmacharia.com/wp-content/uploads/2022/05/encrypted_shared_pref-2048x1114.jpg)

## More
> [Securing Android Application User Sessions](https://bmacharia.com/securing-android-application-user-sessions/)

