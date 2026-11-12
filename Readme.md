Brewkery ☕
Brewkery is a native Android coffee-ordering app built with Kotlin and Jetpack Compose. Users can browse the menu, customize drinks, manage a cart, and place an order with a ticket ID and preparation status.
Features
- Browse menu items and categories from the provided API.
- Search the menu and filter by category.
- View product details, ratings, ingredients, and images.
- Customize available size, milk, and sugar options.
- Add items to the cart, change quantities, and remove items.
- Calculate subtotal, delivery fee, tax, and total.
- Place an order and display a ticket ID with PREPARING status.
- Return to the home screen and view the active-order tracking banner.
- Show loading, empty, and error states.
Tech stack
- Language: Kotlin
- UI: Jetpack Compose and Material 3
- Architecture: MVVM
- Networking: Retrofit, OkHttp, and Gson
- Asynchronous work and state: Kotlin Coroutines and StateFlow
- Image loading: Coil
- Navigation: Navigation Compose
- Tests: JUnit
API
The app uses the Brewkery menu API:
- Menu: https://raw.githubusercontent.com/VivekShah138/Brewkery/main/data.json
- Item details: https://raw.githubusercontent.com/VivekShah138/Brewkery/main/api/items/{id}.json
Pricing
The cart uses the pricing rules supplied by the assignment:
- Subtotal: sum of each configured item's unit price multiplied by its quantity.
- Delivery fee: $2.50.
- Tax: 8% of the subtotal.
- Total: subtotal + delivery fee + tax.
Customization surcharges are included in the configured unit price.
Project structure
app/src/main/java/com/example/brewkery/
├── data/
│   ├── model/
│   ├── remote/
│   └── repository/
├── navigation/
└── ui/
    ├── screens/
    └── viewmodel/
Run the app
1. Clone the repository and open it in Android Studio.
2. Allow Gradle to sync.
3. Run the app on an emulator or Android device with internet access.
Build a debug APK:
./gradlew assembleDebug
The APK is generated at:
app/build/outputs/apk/debug/app-debug.apk
Run tests:
./gradlew test
Screenshots and demo
Add your captured app screenshots to the screenshots/ folder and your screen recording to demo/. Once those files are committed, you can display them here:
![Home screen](screenshots/home.png)
![Product details](screenshots/product-details.png)
![Cart](screenshots/cart.png)
![Order status](screenshots/order-status.png)
Demo recording: demo/brewkery-demo.mp4
AI usage
AI tools used
- ChatGPT (GPT-6): Used as a coding assistant for implementation guidance, Kotlin/Jetpack Compose code drafts, debugging help, and README preparation.
- Android Studio: Used to edit, build, and run the Android project. It is the development environment, not an AI tool.
Prompts used
The following are lightly edited excerpts of the task prompts used during this project, cleaned up for readability:
1. "Help me build the Brewkery assignment as a native Android application using Kotlin and Jetpack Compose, following the supplied API and required screens."
2. "Implement the cart flow with item customizations, quantity controls, and the required subtotal, $2.50 delivery fee, 8% tax, and final total."
3. "Correct and merge the active-order tracking feature into my existing HomeScreen.kt, preserving the current screen and connecting it to the order status screen."
What AI got right
ChatGPT helped break the app into data models, Retrofit API services, a repository, ViewModels, and Compose screens. This gave the project a clear MVVM structure and helped implement the menu, cart, and order flow.
What AI got wrong, and how it was fixed
An early setup missed the Android internet permission, which is required for Retrofit to fetch the remote menu. I added the permission to app/src/main/AndroidManifest.xml:
<uses-permission android:name="android.permission.INTERNET" />
The project still needs to be built and tested in Android Studio to verify the complete app flow; code suggestions alone do not prove that the app runs correctly.
Developer
Harsh Raj
B.Tech, Computer Science and Engineering
