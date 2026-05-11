# 📱 Advanced Scientific Calculator (Android - Java)

A high-performance, user-centric scientific calculator developed using **Java** and **Android Studio**. This application goes beyond standard calculators by implementing custom algorithms to handle mathematical edge cases and providing a premium tactile experience.

## 🚀 Key Features

* **Smart Operator Management:** Dynamically analyzes and corrects invalid user inputs (e.g., preventing `++`, `*/`, or errors like `abs(*(-5`) in real-time to prevent application crashes.
* **Custom nCr & nPr Engine:** Includes a proprietary loop-based logic for Combinations and Permutations, overcoming the limitations of standard libraries for large-number calculations.
* **Haptic Feedback:** Integrated with Android's `VIRTUAL_KEY` constants to provide a realistic "click" sensation upon every button press.
* **Automatic Parentheses Balancing:** An intelligent algorithm detects open parentheses and automatically closes them during the calculation phase.
* **Premium Visual Experience:**
    * **Dual Mode:** Seamlessly toggle between Standard and Scientific layouts with a single tap.
    * **Auto-Scroll:** Integrated `HorizontalScrollView` that automatically tracks the cursor to keep the latest input in view.
    * **Scientific Constants:** Full support for `pi`, `e`, `abs`, `log`, `ln`, `sin`, `cos`, `tan`, `cot`, `rnd`, and more.

## 🛠 Technical Stack

* **Platform:** Android (Java)
* **UI Architecture:** View Binding (For modern, null-safe UI interaction)
* **Math Engine:** `mxparser`
* **Feedback System:** Haptic Feedback API

## 🧩 Engineering Highlight: Intelligent Function Insertion

The core strength of the app lies in its input validation logic, which prevents syntax errors before they happen:

```java
// Smart method that handles signs and auto-multiplication logic:
private void addScientificFunction(String function) {
    if (lastChar == '-') {
        binding.screenTextview.append(function + "(");
    } else if (Character.isDigit(lastChar) || lastChar == ')' || lastChar == 'e' || lastChar == 'i') {
        binding.screenTextview.append("*" + function + "(");
    } else {
        binding.screenTextview.append(function + "(");
    }
}
