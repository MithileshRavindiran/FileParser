# Statement Processor #



* Task

  Write a multi-threaded application in Java 8+ or Kotlin.

  You’re given 2 text files on local filesystem; each should contain a piece of text in UTF-8 (words separated by one or more whitespace characters). The solution should be able to gracefully reject input not conforming to these requirements.

  Process the files and provide the total word occurrence count and separate count in  uploaded files.




**1. Clone the repository**

```bash
 https://github.com/MithileshRavindiran/FileParser.git
```

**2. Run the app using maven**

```bash
mvn spring-boot:run
```

The application can be accessed at `http://localhost:8101`.



**3. API Documentation**


The api documentation is  done at swagger and it can also be accessed at `http://localhost:8101/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config#` .Also tested from swagger end point too


This was developed as a rest application so the uploaded files will provide result in json  format sorted by total count 

format of the response json

```
[
   {
      "word":"string",
      "fileWordsCount":{
         "additionalProp1":0,//file1Name : count on file 1
         "additionalProp2":0,//file2Name : count on file 2
         "additionalProp3":0
      },
      "totalCount":0 //Total Count from all files
   }
]
```


**4. Project Details**

  * Separation of Concerns
  * Facade pattern - Modules are well separated and independent for further Scaling up.
  * Usage of Lombok -Builder pattern which gives a clear picture of separation and implementation.
  * Exception Handling : All the possible constraints are taking into account.
