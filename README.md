# Transaction Explorer API 🚀

A high-performance Spring Boot API designed to navigate and analyze hierarchical transaction data. This project implements graph traversal logic to classify nodes (Root, Leaf, Orphan) and detect cycles within financial data structures.

## ✨ Key Features
* **Graph Classification:** Automatically identifies **Root**, **Leaf**, and **"Pseudo-Root" (Orphan)** nodes.
* **Recursive Discovery:** Traverses the graph to a specified `maxDepth` to retrieve sub-hierarchies.
* **Cycle Detection:** Implements a **Depth-First Search (DFS)** algorithm to catch and prevent infinite loops in data.
* **Transaction Filtering:** Built-in filtering for transaction amounts and types (e.g., SALARY).

## 🛠️ Tech Stack
* **Java 17 / Spring Boot 3.x**
* **Maven** (Build Tool)
* **Postman** (Automated Testing)

## 📋 Getting Started
Clone & Run
  1. git clone https://github.com/vishwajeetkr96-hash/Transaction-Explorer-API.git
  2. cd transaction-explorer-api
  3. ./mvnw spring-boot:run
  4. The API will be available at http://localhost:8080.

### 3. Import Postman Collection
I have included a full test suite in the root directory of this repository to simplify the testing process.

* **File:** `Transaction_Explorer_API.postman_collection.json`
* **How to Import:**
    1.  Open **Postman**.
    2.  Click the **Import** button in the top-left sidebar.
    3.  Drag and drop the `.json` file from your local project folder into the Postman window.

---

### 🧪 Testing Suite
The included Postman collection features **Automated Assertion Scripts**. Every request is automatically validated for:

* **Schema Validation:** Ensuring the JSON response matches the expected structure and data types.
* **Logic Verification:** Confirming that the `isRoot` and `isLeaf` flags correctly reflect the node's position in the graph.
* **Error Handling:** Verifying that the API returns the correct `400` (Bad Request) or `404` (Not Found) status codes for edge cases.

#### Running All Tests
1.  Select the **Transaction Explorer API v1.0** collection in your Postman sidebar.
2.  Click the **Run** button (top right of the collection tab).
3.  Click **Run Transaction Explorer...** to execute the full suite.
4.  Ensure all tests show a green **PASSED** status.

![Postman Test Results](screenshots/Screenshot_560.png)
![Postman Test Results](screenshots/Screenshot_561.png)
![Postman Test Results](screenshots/Screenshot_562.png)
![Postman Test Results](screenshots/Screenshot_565.png)
![Postman Test Results](screenshots/Screenshot_557.png)

### 📂 Project Structure
* **`src/`**: Application source code containing the Graph traversal logic and Spring Boot configuration.
* **`DESIGN.md`**: Technical breakdown of the DFS logic, Orphan handling, and complexity analysis.
* **`Transaction_Explorer_API.postman_collection.json`**: The full API test suite with automated assertions.

> **Note:** For a deep dive into the algorithm choices and complexity analysis, please refer to the [DESIGN.md](./DESIGN.md) file.
