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

### 1. Clone & Run
```bash
git clone [https://github.com/vishwajeetkr96-hash/Transaction-Explorer-API.git](https://github.com/vishwajeetkr96-hash/Transaction-Explorer-API.git)
cd Transaction-Explorer-API
./mvnw spring-boot:run
