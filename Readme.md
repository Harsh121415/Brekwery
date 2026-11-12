# Brewkery ☕

A native Android coffee ordering application built using **Kotlin and Jetpack Compose**. Brewkery allows users to explore a coffee menu, customize drinks, add products to a cart, and place orders with a ticket ID and preparation status.

## Features

- **Home Screen:** Browse menu items, categories, ratings, prices, and announcements.
- **Search and Filter:** Search drinks and filter by category.
- **Product Details:** View product images, descriptions, ingredients, and ratings.
- **Dynamic Customization:** Select drink size, milk option, and sugar level.
- **Cart:** Add items, update quantities, remove products, and view the bill.
- **Bill Calculation:** Automatically calculate subtotal, delivery fee, tax, and final total.
- **Order Confirmation:** Generate a unique ticket ID and display PREPARING status.
- **Order Tracking:** View the active order from the home screen.
- **Loading and Error States:** Handle menu loading and API failures.

## Tech Stack

| Technology | Usage |
|---|---|
| Kotlin | Primary programming language |
| Jetpack Compose | Native Android UI |
| Material 3 | UI components |
| MVVM | Application architecture |
| Retrofit | API communication |
| Gson | JSON parsing |
| Kotlin Coroutines | Asynchronous operations |
| StateFlow | Reactive UI state |
| Coil | Image loading |
| Navigation Compose | Screen navigation |
| JUnit | Unit testing |

## API Integration

Menu API:

`https://raw.githubusercontent.com/VivekShah138/Brewkery/main/data.json`

Single item API:

`https://raw.githubusercontent.com/VivekShah138/Brewkery/main/api/items/{id}.json`

The application fetches menu categories and products from the provided API. Product details are loaded using the corresponding item ID.

## Pricing Calculation

- Subtotal = Sum of item prices × quantities
- Delivery Fee = $2.50
- Tax = 8% of subtotal
- Final Total = Subtotal + Delivery Fee + Tax

Customization prices are included in the selected item's unit price.

## Project Architecture

```text
com.example.brewkery
│
├── data
│   ├── model
│   ├── remote
│   └── repository
│
├── navigation
│
├── ui
│   ├── screens
│   └── viewmodel
│
└── MainActivity.kt
```

## Setup Instructions

1. Clone this repository.
2. Open the project in Android Studio.
3. Allow Gradle to sync.
4. Connect an Android device or start an emulator.
5. Run the application.

```bash
git clone YOUR_GITHUB_REPOSITORY_URL
```

Build the debug APK:

```bash
./gradlew assembleDebug
```

APK output:

`app/build/outputs/apk/debug/app-debug.apk`

## Testing

Run the unit tests using:

```bash
./gradlew test
```

The cart calculation tests cover subtotal, tax, delivery fee, final total, and empty-cart calculations.

## AI Tools Used

AI assistance was used during development for code scaffolding, implementation guidance, debugging, and documentation.

### Representative prompts

1. "Build a native Android coffee ordering application using Kotlin and Jetpack Compose with an MVVM architecture and Retrofit API integration."
2. "Implement a cart system with quantity updates, customization options, delivery fee, tax calculation, and final bill."
3. "Implement order confirmation with a unique ticket ID, PREPARING status, and active order tracking."

*Replace these representative prompts with the exact prompts you used if your submission requires verbatim prompt history.*

### What AI got right

AI helped structure the application into models, API services, repositories, ViewModels, and Compose screens. It also helped implement the cart calculation logic and order flow.

### What AI got wrong and how it was fixed

The app initially had a connectivity issue because the Android manifest was missing the Internet permission. The manifest was updated with:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

The app was then prepared for further build and runtime verification.

## Developer

Harsh Raj

B.Tech Computer Science and Engineering
