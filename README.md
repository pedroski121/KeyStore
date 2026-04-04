# Keystore

## Overview
Keystore is a distributed key-value store implemented in Java.

## Current Features
- **Consistent Hash Ring**: Dynamically distributes keys across nodes, minimizing data movement when nodes join or leave.
- **Vector Clocks**: Tracks causality and resolves conflicts between different versions of data.

## Consistent Hash Ring
Consistent hashing is a technique to distribute data across a cluster in a way that minimizes reorganization when nodes are added or removed. Each node is assigned one or more positions (virtual nodes) on a hash ring. Keys are mapped to nodes based on their hash value, ensuring even distribution and scalability.

- **NodeInfo**: Represents a physical node in the cluster.
- **VirtualNode**: Allows a physical node to appear multiple times on the ring for better load balancing.
- **ConsistentHashRing**: Manages the ring, node addition/removal, and key-to-node mapping.

## Vector Clocks
Vector clocks are used to track the version history of each key-value pair. Each node maintains a vector of counters, one for each node in the system. When a value is updated, the node increments its own counter. Vector clocks allow the system to detect and resolve conflicts caused by concurrent updates.

- **VectorClock**: Maintains the version history for each value.
- **VersionedValue**: Associates a value with its vector clock.
- **ComparisonResult**: Enum for comparing vector clocks (BEFORE, AFTER, CONCURRENT, EQUAL).

## Project Structure
- `src/main/java/com/base/hashring/` — Consistent hash ring implementation
- `src/main/java/com/base/kvstore/` — Key-value store logic and vector clock classes
- `src/test/java/com/base/hashring/` — Tests for the hash ring
- `src/test/java/com/base/kvstore/` — Tests for vector clocks

## Getting Started
1. **Build the project:**
   ```sh
   mvn clean package
   ```
2. **Run tests:**
   ```sh
   mvn test
   ```
3. **Run the application:**
   ```sh
   java -cp target/classes com.base.kvstore.Main
   ```

## Requirements
- Java 8 or higher
- Maven


