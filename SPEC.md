# Test Case Editor — Specification

## Overview

A desktop application for defining and managing test cases using a decision table approach.
Built with Java 17, no third-party libraries (Swing UI).

---

## Core Concepts

### Field Library (Global)

All field definitions are shared across test suites. Defined once, reused anywhere.

**Group**
- A logical container for related fields.
- Can represent anything: a form, a request body, a DB row, a config block, etc.
- Properties: `name`

**Field**
- Belongs to one Group.
- Has a fixed set of allowed values defined by the user.
- Properties: `name`, `allowedValues: List<String>`
- Reserved values available in all fields: `-` (don't care)

**Value**
- A discrete option for a Field (e.g. `A`, `B`, `Blank`, `Admin`).
- `Blank` is a user-defined value meaning explicitly empty input.
- `-` means "don't care" / irrelevant to the case outcome.

---

### Test Suite

A named collection of test cases sharing the same condition and result structure.

**ConditionColumn**
- References a specific `Group → Field` from the Field Library.
- Determines which fields appear as columns in the test grid.

**ResultColumn**
- A named expected outcome.
- Values: free text, or a predefined list of options (optional).
- Properties: `name`, `allowedValues: List<String>` (optional)

**TestCase**
- One row in the test grid.
- Maps each `ConditionColumn` to one of the field's allowed values (or `-`).
- Maps each `ResultColumn` to a value (free text or predefined).

---

## Data Model

```
Project
├── FieldLibrary
│   └── Group[]
│         └── Field[]
│               └── String[] allowedValues
│
└── TestSuite[]
      ├── name: String
      ├── ConditionColumn[]
      │     └── ref → Group + Field
      ├── ResultColumn[]
      │     └── name + optional allowedValues[]
      └── TestCase[]
            ├── conditionValues: Map<ConditionColumn, String>
            └── resultValues:    Map<ResultColumn, String>
```

---

## Application UI

### Layout

Two top-level tabs:

```
[ Field Library ]  [ Test Suites ]
```

---

### Tab: Field Library

Define Groups and Fields that can be reused across all test suites.

```
+--------------------------------------------------+
|  [+ Group]                                        |
|                                                   |
|  > [Login Form]                        [✎] [✗]   |
|      status   [A] [B] [Blank]  [+ Value] [✗]     |
|      role     [Admin] [Guest]  [+ Value] [✗]     |
|                                                   |
|  > [HTTP Request]                      [✎] [✗]   |
|      method   [GET] [POST]     [+ Value] [✗]     |
+--------------------------------------------------+
```

- `[✎]` inline rename
- `[✗]` delete (warns if field is in use by a test suite)
- Values are shown as removable chips/tags

---

### Tab: Test Suites

Left: list of suites. Right: selected suite editor.

```
+-------------------------+-----------------------------------------------+
|  Test Suites            |  Suite: "Login Happy Path"            [✎]     |
|  ─────────────────────  |  ─────────────────────────────────────────    |
|  > Login Happy Path     |  Conditions  [+ Add Field ▼]                  |
|  > API Role Check       |    ● Login Form / status                 [✗]  |
|  [+ Suite]              |    ● Login Form / role                   [✗]  |
|                         |                                               |
|                         |  Results  [+ Result]                          |
|                         |    ● Redirect Page                       [✗]  |
|                         |    ● Error Message                       [✗]  |
|                         |  ───────────────────────────────────────────  |
|                         |         | status | role  | Redirect | Error   |
|                         |  Case 1 | A    ▼ | Admin▼| /dash    | -       |
|                         |  Case 2 | B    ▼ | Guest▼| /error   | Invalid |
|                         |  [+ Case]          [Render ▼] [Export ▼]      |
+-------------------------+-----------------------------------------------+
```

**Test grid behavior:**
- Condition cells → dropdown with the field's defined values + `-`
- Result cells → free text input, or dropdown if `allowedValues` is set
- `[+ Case]` adds a new empty row
- Columns follow the order of defined Conditions and Results

---

## Render Views

Accessed via `[Render ▼]` — opens a read-only view window, does not affect editing.

### Decision Table View

Each test case is a column. Condition values are expanded as individual rows marked with:
- `●` — this value is active for the case
- `✗` — this value does not apply
- `-` — don't care

```
                         | Case 1 | Case 2 | Case 3 |
-------------------------|--------|--------|--------|
CONDITIONS               |        |        |        |
  [Login Form]           |        |        |        |
    status               |        |        |        |
      A                  |   ●    |   ✗    |   -    |
      B                  |   ✗    |   ●    |   -    |
      Blank              |   ✗    |   ✗    |   ●    |
    role                 |        |        |        |
      Admin              |   ●    |   ●    |   -    |
      Guest              |   ✗    |   ✗    |   ●    |
-------------------------|--------|--------|--------|
RESULTS                  |        |        |        |
    Redirect Page        | /dash  | /error | /login |
    Error Message        | -      | Invalid| -      |
```

---

## Export System

### Exporter Interface

```java
public interface Exporter {
    String id();             // unique key, e.g. "csv"
    String displayName();    // shown in UI, e.g. "CSV"
    String fileExtension();  // e.g. "csv", "html", "txt"
    String export(TestSuite suite, FieldLibrary library);
}
```

### Built-in Exporters

| ID               | Format           | Description                          |
|------------------|------------------|--------------------------------------|
| `csv`            | `.csv`           | Flat table, one row per test case    |
| `html`           | `.html`          | Styled table with group headers      |
| `decision_table` | `.txt`           | ASCII decision table (●/✗/- format)  |

### ExporterRegistry

```java
public class ExporterRegistry {
    public void register(Exporter exporter);
    public List<Exporter> all();
    public Optional<Exporter> get(String id);
}
```

Registered at startup:

```java
registry.register(new CsvExporter());
registry.register(new HtmlExporter());
registry.register(new DecisionTableExporter());
```

### Adding a Custom Exporter

**Option A — Code registration:**

```java
registry.register(new MyCustomExporter());
```

**Option B — ServiceLoader (zero startup code change):**

Create file:
```
META-INF/services/com.editor.export.Exporter
```
With content:
```
com.mycompany.MyCustomExporter
```

The application auto-discovers it on startup.

---

## Project File

Save/load the full project (Field Library + all Test Suites) as a single `.tcep` file (plain JSON, no external parser needed — hand-written serialization).

---

## Module Structure

```
src/
├── Main.java
├── model/
│   ├── Field.java
│   ├── Group.java
│   ├── FieldLibrary.java
│   ├── ConditionColumn.java
│   ├── ResultColumn.java
│   ├── TestCase.java
│   └── TestSuite.java
├── ui/
│   ├── MainWindow.java
│   ├── FieldLibraryPanel.java
│   ├── TestSuiteListPanel.java
│   ├── TestSuiteEditorPanel.java
│   ├── TestCaseGrid.java
│   └── RenderWindow.java
├── export/
│   ├── Exporter.java
│   ├── ExporterRegistry.java
│   ├── CsvExporter.java
│   ├── HtmlExporter.java
│   └── DecisionTableExporter.java
└── persistence/
    ├── ProjectSerializer.java
    └── ProjectDeserializer.java
```

---

## Build

Gradle project, Java 17 toolchain, no third-party dependencies.

**`build.gradle`**

```groovy
plugins {
    id 'java'
    id 'application'
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

application {
    mainClass = 'com.editor.Main'
}

compileJava.options.encoding = 'UTF-8'

repositories {
    // no external repos needed
}

dependencies {
    // none
}

jar {
    manifest {
        attributes 'Main-Class': 'com.editor.Main'
    }
    // fat jar — no deps, so single jar is sufficient
}
```

**`settings.gradle`**

```groovy
rootProject.name = 'TestCaseEditor'
```

**Project layout**

```
TestCaseEditor/
├── build.gradle
├── settings.gradle
├── SPEC.md
└── src/
    └── main/
        ├── java/
        │   └── com/editor/
        │       ├── Main.java
        │       ├── model/
        │       ├── ui/
        │       ├── export/
        │       └── persistence/
        └── resources/
            └── META-INF/services/
                └── com.editor.export.Exporter   ← optional, for ServiceLoader
```

**Run**

```bash
./gradlew run
```

**Build fat jar**

```bash
./gradlew jar
java -jar build/libs/TestCaseEditor.jar
```

---

## Constraints

- Java 17 only
- No third-party libraries
- Build tool: Gradle (wrapper committed to repo)
- UI: Java Swing
- Persistence: hand-written JSON (no Jackson/Gson)
- Export: pure string building (no template engines)
