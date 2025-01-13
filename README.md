## EX2 Project

# Overview

Very basic Java spreadsheet application that includes support for text, numbers, and basic formulae.

 Main Features:

- Cels content could contain numerals and alphanumeric text

- Basic math operations may be included in formulas such as addition, multiplication, division and subtraction.

- Cells in the spreadsheet that contain information can be added and referenced such as the value contained in cell A1 or B1.

- Load and save any spreadsheet files made.


 Example Usage:

```java

Ex2Sheet sheet = new Ex2Sheet(10, 10);  // Instantiate a 10 by 10 Exel sheet.

sheet.set(0, 0, "5");                   // Set the value in 0 row and 0 column to be 5.

sheet.set(1, 0, "=A0+3");              // the value of A0 in B0 is equivalent to: A0 + 3.

sheet.save("mysheet.csv");             // A CSV file can be downloaded or shared for future work and editing. 

```

