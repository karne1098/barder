# Sprint 2: Maps

## Project Details

### Project name - Maps (Sprint 5)
### Project details
Total time: 25 hours 
### Team members and contributions
Karen Hu (khu21), Kathryn Lee (klee161)

### Link to Repository
https://github.com/cs0320-s2023/sprint-3-jliu238-klee161

## Design choices

1. `bbox` package contains all the classes and subpackages involved in boundary boxes
  - `Bbox` class that represents a boundary box, and contains a function "contains" that check if given coordinates are contained within the boundary box. If a developer passes in an empty list of coordinates, the function will return true (though an empty list is actually an insufficient amount of information.)
- `caching` package
  - `cache` class that stores the map from incoming request to weather data
  - `haversine` class that stores that calculations for the haversine function, used to calculate the distance between two lat,lon pairs
- `weather` package
  - `Forecast` holds the forecast json as an object to retrieve the properties
  - `ForecastForTime` holds the forecast json as an object to retrieve the updated time
  - `ForecastProperties` is used to retrieve the forecast periods 
  - `Periods` is used to retrieve the temp and units
  - `WeatherMetaData` is used to hold the initial NWS API JSON as an object
- `IncomingRequest` class creates a incoming request from a lat, lon pair
- `NWSRequest` class creates a request through the NWS api using the incoming request
- `NWSResponse` class stores that weather information for each incoming request
2. `server` package: contains all the classes and subpackages involved in handling requests made to server endpoints
- `handlers` package: holds the four handler classes that the server hosts endpoints for ('loadcsv', 'viewcsv', 'searchcsv', and 'weather')
    - `LoadHandler` handles requests made to the 'loadcsv' endpoint
    - `SearchHandler` handles requests made to the 'searchcsv' endpoint
    - `ViewHandler` handles requests made to the 'viewcsv' endpoint
    - `WeatherHandler` handles requests made to the 'weather' endpoint
- `Server` class creates the port and calls all the handlers
- `Variables class` holds our global variables
3. `creatorfromrow` package: contains all the classes, interfaces, and subpackages involved in creating objects from parsed file rows
4. `csv` package: contains all the classes and subpackages involved in parsing and searching a given reader object
  
### Relationship between Classes and Interfaces
- `Route` interface is implemented in all the handlers (`LoadHandler`, `ViewHandler`, `SearchHandler`, `WeatherHandler`)
- Relationship between `IncomingRequest`, `NWSRequest`, and `NWSResponse` classes:
  1. An `IncomingRequest` object is made form a lat, lon pair
  2. This object is used to start an NWSRequest, where the data is being retrieved from the NWS API
  3. The data is then stored in an NWSResponse object for the corresponding IncomingRequest object
- Relationship between `Forecast`, `ForecastForTime`, `ForecastProperties`, `Periods`, and `WeatherMetaData`
  - `WeatherMetaData` is the object we deserialize from the JSON that we retrieved through "https://api.weather.gov/points/" using a latitude-longitude pair input
  - We go into the 'properties' field of `WeatherMetaData` to obtain the forecast link, which leads to another JSON that is represented by `Forecast` and `ForecastForTime`, which we can deserialize that JSON and create the two objects
  - From here, we access the properties of the `Forecast` class where we can retrieve the updated time and the periods, which is represented by a `Periods` object obtained by deserializing the 'periods' field
  - Within the periods, we can access the temperature and units for a given latitude-longitude pair at the current time
- `WeatherHandler` creates an `IncomingRequest` object and uses `Cache` to call `retrieveResponse`. Within `retrieveResponse`, we call NWSResponse.getResponse, where we use an `NWSRequest` object to call its functions to extract temperature, units, and updated time.
- `SearchHandler` and `ViewHandler` rely on `LoadHandler` fulfilling a request for a valid file to be able to view or search a file
- `Server` relies on `Variables` and all the handlers in the handlers package to store global data and fulfill requests/return responses made for a given endpoint.

### Data Structures Used
`Variables`:
- Within the Variables class, we have 4 global variables
  - `loadedFile` is a List<List<String>> which represents the parsed, load file
  - `loadedFileHasHeader` is a boolean that represents whether the loaded file has a header
  - `existingFilePaths` is a Map<String, Boolean> that maps file paths to a boolean representing whether that file has a header
  - `alreadyLoadedFiles` is a Map<String, Parser> that maps file paths to the already parsed file, if it has been previous loaded and parsed
`Cache`:
  - We used the Google Guava library to create a loading cache, which is similar to a hashmap where it maps incoming requests to the corresponding weather data. However, the loading cache allows for cache-matching and cache-evicting after a certain amount of time (chosen by the developer-user).
  - 
## Errors/Bugs
No known errors or bugs currently, we caught most of them while testing. 

### CheckStyle Errors
No CheckStyle errors found. 

## Tests
- `CacheTest`
  - tests that cache works when similar and very different incoming requests are made
  IncomingRequestTest
  - test that an incoming request is properly made form a lat,lon pair
- `NWSRequestTest`
  - unit test for the NWSRequest class on both mocked jsons and real incoming requests
  - made sure data was properly retrieved from mocked jsons
- `NWSResponseTest`
  - unit test for the NWSResponse class on both mocked jsons and real incoming requests
  - made sure data was properly retrieved from mocked jsons
- `LoadHandlerTest`
  - integration test for LoadHandler
- `ViewHandlerTest`
  - integration test for ViewHandler
- `SearchHandlerTest`
  - integration test for SearchHandler
- `IntegrationTest`
  - tests various sequences of using `LoadHandler`, `ViewHandler`, and `SearchHandler`
- `UnitTest`
  - tests the responses returned by the handlers of the endpoints provided through the server
## How to..
### Run the tests
1. Go into the project directory through the terminal.
2. Run mvn test (can use `a` to run all tests or `p` to specify any particular testing suites you'd like to run.)
### Build and run the program
1. Start up the server by navigating to the server package, opening Server.java, and clicking the green play button for main.
- FOR THE DEVELOPER-USER: please configure the cache-matching and cache-eviction criteria by filling in the `distance` (km), `time` (minutes), `size` parameters for the `Cache` object initialized in line 27 (currently set at 24 hours, 5 km, and size 100).
2. Open your preferred browser and make requests using our specifed port number `3220`. The local host base link is `http://localhost:3220`.
3. Make your requests using the local host base link. The available endpoints include `loadcsv`, `viewcsv`, `searchcsv`, and `weather`:
   - To load a file: `[base link]/loadcsv?filepath=[INSERT FILE NAME HERE]`
   - To view a loaded file: `[base link]/viewcsv`
   - To search a loaded file: 
     - Overall: `[base link]/searchcsv?value=[INSERT VALUE HERE]&searchByColumn=[INSERT BOOLEAN HERE]`
     - By specified column: `[base link]/searchcsv?value=[INSERT VALUE HERE]&searchByColumn=[INSERT BOOLEAN HERE]=&columnIndicator=[INSERT INDICATOR HERE]`
   - To view the current temperature in units for a given location: `[base link]/weather?lat=[INSERT LATITUDE HERE]&lon=[INSERT LONGITUDE HERE]`

## References and Citations
Thank you `afunk3`, `ahsieh10`, `mke2`, `pramach3` for your helpful code documentation and open repos, as well as
Tim for his lecture notes on defensive programming and caching.



