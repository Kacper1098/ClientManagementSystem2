# ClientManagementSystem2
ClientManagementSystem2 is extended version of ClientManagementSystem project with various improvements of model classes and data management.
It allows user to:

* Choose currency from National Bank of Poland in which product prices will be shown.

* Generate files with data to manage in further options.

* Data managements options: 
    * Find customer that bought the most products,
    * Find customer that spent the most money,
    * Show juxtaposition with product and number of purchases of that product,
    * Show product with the highest number of purchases,
    * Show product with the lowest number of purchases,
    * Show juxtaposition with category and number of purchases of this category,
    * Send email for each client with products they have bought
    
## Installation
    
 * From _ClientManagementSystem2_ module: 
    ```bash
        mvn clean install
    ``` 
 * From _main_ module
    ```bash
        mvn clean compile assembly::single
    ```
    
## Usage
    
 * From _main/target_ 
    ```bash
        java --enable-preview -cp main-1.0-SNAPSHOT-jar-with-dependencies.jar stefanowicz.kacper.main.App
    ```
    
Please make sure that _files_ folder is located in the same folder as _main-1.0-SNAPSHOT-jar-with-dependencies.jar_
