# Video Streaming API

Streaming is the technique of sending small portions of data (chunks) in separate requests. It is widely used in modern streaming online services and allows a variety of options on the client side such as seeking or forwarding in videoplayers.

Getting started
technologies we are going to use:

* Java 17
* Spring boot
* Maven
* Docker
* MongoDB
* MinIO
* Postman — API tool we will be using for consuming our
* Web browser — In order to verify that our application is really working

## 206 HTTP Code (Partial Content)
This application implements a video streaming backend application that supports 206 http status code

## Why are we going to use two databases ?

### We are going to save metadata in MongoDB:

MongoDB - MongoDB is a tool that can manage document-oriented information, store or retrieve information. MongoDB is used for high-volume data storage, helping organizations store large amounts of data while still performing rapidly.

### We are going to save the video in MinIO:

From the MinIO official website https://min.io/ MinIO is a high-performance, S3 compatible object store. It is built for
large scale AI/ML, data lake and database workloads. It runs on-prem and
on any cloud (public or private) and from the data center to the edge.

### Docker

We will run MongoDB and MinIO in docker, so we have our docker compose file located in /docker/docker-compose.yaml

## Postman Collection

You can import the file Video Streaming.postman_collection.json to your postman to access the following services:

### Get list of available genre

```
GET /api/genres
```

Response example:
```javascript
[
    "DRAMA",
    "FANTASY",
    "HORROR",
    "ROMANCE",
    "THRILLER",
    "ACTION",
    "COMEDY",
    "CRIME",
    "SCIENCE_FICTION",
    "ANIMATION",
    "ADVENTURE",
    "DOCUMENTARY"
]
 ```

### Create metadata

```
POST /api/v1/videos
```

RequestBody example: 
```javascript
{
  "title":"Movie",
  "synopsis":"Synopsis",
  "director":"Director",
  "cast": {
  "Aldo" : "Jim"
  },
  "yearOfRelease":"1994",
  "genre" : ["ACTION", "COMEDY"]
}
 ```

Response example:
```
"26351743-abd6-4362-a0d3-4e7ffb8d3c60"
 ```

### Update metadata

```
PUT /api/v1/videos/{id}
 ```

RequestBody example:
```javascript
{
  "title":"Movie",
  "synopsis":"Synopsis",
  "director":"Director",
  "cast": {
  "Aldo" : "Jim"
  },
  "yearOfRelease":"1994",
  "genre" : ["ACTION", "COMEDY"]
}
 ```

Response example:
```javascript
"26351743-abd6-4362-a0d3-4e7ffb8d3c60"
 ```

### Get videos

Search for videos based on some search/query criteria (e.g.: Movies directed by a
specific director)

```
GET /api/v1/videos
 ```

From-Data param examples:
```
yearOfRelease
genre
director
title
 ```

Response example:
```javascript
{
    "actualPage": 0,
    "totalItems": 1,
    "totalPage": 1,
    "videos": [
        {
            "id": "cd9b8c97-4d9a-4dee-87b5-c9058aa30576",
            "videoUrl": "http://localhost:8080/api/v1/videos/play/cd9b8c97-4d9a-4dee-87b5-c9058aa30576",
            "title": "Test 1",
            "synopsis": "",
            "director": "Fernando",
            "yearOfRelease": 2022,
            "genre": [
                "FANTASY",
                "COMEDY"
            ],
            "runningTime": "00:00:10",
            "httpContentType": "video/mp4",
            "size": 788493,
            "hasVideo": true,
            "cast": {
                "Fernando": "Amaro",
                "Rafael": "Eduardo"
            }
        }
    ]
}
 ```


### Get video

```
GET /api/v1/videos/{id}
```

Response example:
```javascript
{
    "id": "26351743-abd6-4362-a0d3-4e7ffb8d3c60",
    "videoUrl": null,
    "title": "Test 1",
    "synopsis": "",
    "director": "Fernando",
    "yearOfRelease": 2022,
    "genre": [
        "FANTASY",
        "COMEDY"
    ],
    "runningTime": "00:00:00",
    "httpContentType": null,
    "size": null,
    "hasVideo": false,
    "cast": {
        "Fernando": "Amaro",
        "Rafael": "Eduardo"
    }
}
 ```

### Delete video

```
DELETE /api/v1/videos/{id}
```

Response example:
```
Video deleted successfully
 ```

### Get engagement statistic

```
GET /api/engagement-statistic
```

From-Data param example:
```
userIdentifier
 ```

Response example:
```
[
    {
        "id": "1b8d9a6f-9046-457a-9f6f-fbc97e4825d6",
        "userIdentifier": "584b7739-cb58-4da5-9eb3-c4947a9ee3ed",
        "videoIdentifier": "75baa91c-571d-4dd4-84cf-a3bee5304115",
        "impressions": 4, //A client loading a video.
        "views": 2 //A client playing a video.
    }
]    
```

### Upload video

Upload video is not available in Streaming.postman_collection.json because we need to split the file and just send it by chunk and postman does not have support to do it automatically,
for this situation we have created a html file to test file upload located in src/main/resources/static/file-upload-test.html.
In this html page we have a JS code that split the file to us and send it by chunk and we just need to put the Id that is returned when we create a metadata and we need to select a media. We have a video example located in src/main/resources/static/test-video.mp4.
After you select a video and put a metadata Id in Id field you can click on upload button. 

How to load file-upload-test.html ?

You can access http://localhost:8080/api/page-upload using your browser.

When you select a file and put id in the form and hit Upload button basically we are calling the following service:

```
POST /api/v1/videos/upload-file
```
RequestParam example:
```
MultipartFile file
UUID id
String type
int chunkNumber
int totalChunks
 ```

### Play video

To play a video you can go to your browser and open http://localhost:8080/api/v1/videos/play/{id}

```
GET /api/v1/videos/play/{id}
```

### How to run the project ?

First you need to have installed and configured java 17, maven and docker.

- Download repo
- Go to docker folder
- Open terminal in docker folder and run ```docker-compose up -d``` to start MongoDB and MinIO
- Go to root folder where we have pom.xml file and open terminal in this folder and execute ```mvn clean install``` and after clean install you can run ```mvn spring-boot:run```