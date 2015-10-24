# API Details
## Environments
* HOST: jointrump.com (AWS)
* PORT: 9000

## Collection Schema
### Activity Schema
```
{
    "activities" : [
        {
            "fromUser": <string>,
            "activityId": <string>,
            "activityType": "JOIN",
            "description": <string>,
            "likeCount": <int>,
            "createTime": <long>,
            "comments": [],
            "likedBy": []
        },
        ...
    ]
}
```

### Challege Schema
```
{
    "challengeId": <string>,
    "title": <string>,
    "description": <string>,
    "startDate": <long>,
    "endDate": <long>,
    "center": {
        "latitude": <double>,
        "longitude": <double>
    },
    "gift": {
        "giftId": <string>,
        "giftName": <string>,
        "amount": <double>,
        "description": <string>,
        "maxGift": <int>,
        "givenGift": <int>,
        "giftUrl": <string>,
        "merchantId": <string>,
        "merchant": <string>
    },
    "isLocal": <boolean>,
    "challengeType": "QRCode",
    "isEnabled": <boolean>,
    "QRCode": <string>,
    "isShareRequired": <boolean>,
    "isPicRequired": <boolean>,
    "imageUrl": <string>,
    "disclaimer": <string>,
    "comments": [],
    "likedBy": [],
    "steps": [
        "",
        "",
        ""
    ]
}
```

## Routes
### Token Routes
#### Getting Token
* Method : POST
* Route : `http://<HOST>:<PORT>/token`
* Sample Request:
```
{
    "customerKey": "e1uo9pq8j208ghvfsckphi04tr",
    "secret": "9u6p43kq1osnb84un6qppsjji0"
}
```
* Sample Response:
```
{
    "accessToken": "mHTY8qZgiV2bRdDYzfz3geJ3lnM="
}
```

### Gift Routes
#### Adding Gift
* Method : POST
* Route : `http://<HOST>:<PORT>/gift`
* Sample Request
```
{
    "giftName": "testGift",
    "amount": 100,
    "description": "Papa John's Gift",
    "maxGift": 20,
    "giftUrl": "",
    "merchantId": "5477ef3a2276b5a9027cacf6"
}
```
* Sample Response
```
{
    "giftId": "547ccc9f227664a191a720e4",
    "giftName": "testGift",
    "amount": 100,
    "description": "Papa John's Gift",
    "maxGift": 20,
    "givenGift": 0,
    "giftStatus": "ADDED",
    "giftUrl": "",
    "merchantId": "5477ef3a2276b5a9027cacf6",
    "merchant": "Bass Pro Shops"
}
```

#### Getting Gift
* Method : GET
* Route : `http://<HOST>:<PORT>/gift/<giftId>`
* Sample Response:
```
{
    "giftId": "547c0c8b227664a191a720de",
    "giftName": "testGift",
    "amount": 100,
    "description": "Papa John's Gift",
    "maxGift": 20,
    "givenGift": 1,
    "giftStatus": "ADDED",
    "giftUrl": "",
    "merchantId": "5477ef3a2276b5a9027cacf6",
    "merchant": "Bass Pro Shops"
}
```

#### Getting Gift Image URL
* Method : GET
* Route : `http://<HOST>:<PORT>/gift/<giftId>/giftImage`
* Response:
```
{
    "giftURL" : "http://jointrump.com/img/image-1.jpg";
}
```	

#### Updating Gift
* Method : PUT
* Route : `http://<HOST>:<PORT>/gift/<giftId>`
* Sample Request:
```
{
    "customerKey": "e1uo9pq8j208ghvfsckphi04tr",
    "accessToken": "mHTY8qZgiV2bRdDYzfz3geJ3lnM=",
    "giftStatus" : "FUNDED"
}
```
* Sample Response:
```
{
    "giftId": "547c0c8b227664a191a720de",
    "giftName": "testGift",
    "amount": 100,
    "description": "Papa John's Gift",
    "maxGift": 20,
    "givenGift": 1,
    "giftStatus": "FUNDED",
    "giftUrl": "",
    "merchantId": "5477ef3a2276b5a9027cacf6",
    "merchant": "Bass Pro Shops"
}
```

#### Getting All Gifts
* Method : GET
* Route : `http://<HOST>:<PORT>/gifts`
* Sample Response :
```
{
    "gifts": [
        {
            "giftId": "547ccc9f227664a191a720e4",
            "giftName": "testGift",
            "amount": 100,
            "description": "Papa John's Gift",
            "maxGift": 20,
            "givenGift": 0,
            "giftStatus": "ADDED",
            "giftUrl": "",
            "merchantId": "5477ef3a2276b5a9027cacf6",
            "merchant": "Bass Pro Shops"
	    },
        ...
    ]
}   
```

### Challenge Routes
#### Add Challenge
* Method : POST
* Route : `http://<HOST>:<PORT>/challenge/<merchantId>`
* Sample Request
```
{
    "title": "test challenge4",
    "description": "test description4",
    "startEpoch": 1388634505001, // long, optional
    "endEpoch": 1414900105001, // long, optional
    "lattitude": 123457, // double, optional
    "longitude": 789013, // double, optional
    "QRCode": "testQR4",
    "giftId": "5455ed4e3004b4805ac192f1",
    "step1Desc": "step 1 desc",
    "step2Desc": "step 2 desc",
    "step3Desc": "step 3 desc",
    "imgUrl": "some url4",
    "disclaimer": "some disclaimer4"
}
```
* Sample Response
```
{
    "challengeId": "5455edfa3004b4805ac192f2",
    "title": "test challenge4",
    "description": "test description4",
    "startDate": 1388634505001,
    "endDate": 1414900105001,
    "lattitude": 123457,
    "longitude": 789013,
    "gift": {
        "giftId": "5455ed4e3004b4805ac192f1",
        "giftName": "testGift",
        "amount": 10,
        "description": "test starbucks card",
        "maxGift": 5,
        "givenGift": 0,
        "merchant": "Starbucks"
    },
    "isLocal": true,
    "challengeType": "QRCode",
    "isEnabled": true,
    "QRCode": "testQR4",
    "isShareRequired": true,
    "isPicRequired": true,
    "imageUrl": "some url4",
    "disclaimer": "some disclaimer4",
    "steps": [
        "step 1 desc",
        "step 2 desc",
        "step 3 desc"
    ]
}
```

#### Finish Challenge
* Method : GET
* Route : `http://<HOST>:<PORT>/challenge/<challengeId>/finish/<userId>`
* Sample Response :
```
```

#### Get Small Cluster
* Method : POST
* Route : `http://<HOST>:<PORT>/smallCluster`
* Sample Request
```
{
    "clusterId": <string>,
    "userId": <string>
}
```
* Sample Response :
```
{
    "clusterId": "547d5d9fe4b008334921ff90",
    "center": {
        "latitude": 0.1,
        "longitude": 2.2
    },
    "numberOfChallenges": 2,
    "challenges": [
        {
            "challengeId": "547d5d9fe4b008334921ff8f",
            "title": "Papa John's Challenge",
            "description": "Cheese Burst Pizza Burraahhh",
            "startDate": 1388634505001,
            "endDate": 1414900105001,
            "center": {
                "latitude": 0.1,
                "longitude": 2.2
            },
            "gift": {
                "giftId": "547d5517e4b008334921ff8d",
                "giftName": "testGift",
                "amount": 100,
                "description": "Papa John's Gift",
                "maxGift": 20,
                "givenGift": 0,
                "giftStatus": "ADDED",
                "giftUrl": "",
                "merchantId": "547d1861e4b008334921ff8a",
                "merchant": "merchant A"
            },
            "isLocal": true,
            "challengeType": "QRCode",
            "isEnabled": true,
            "QRCode": "UkwtMTAwMQ==",
            "isShareRequired": true,
            "isPicRequired": true,
            "imageUrl": "some url4",
            "disclaimer": "some disclaimer4",
            "comments": [],
            "likedBy": [],
            "steps": [
                "",
                "",
                ""
            ]
        },
        ...
    ]
}
```

#### Get Large Cluster
* Method : POST
* Route : `http://<HOST>:<PORT>/largeCluster`
* Sample Request
```
{
    "longitude": <double>,
    "latitude": <double>
}
```
* Sample Response :
```
{
    "clusterId": "547d5d9fe4b008334921ff91",
    "center": {
        "latitude": 0.1,
        "longitude": 2.2
    },
    "smallClusters": [
        {
            "clusterId": "547d5d9fe4b008334921ff90",
            "center": {
                "latitude": 0.1,
                "longitude": 2.2
            },
            "numberOfChallenges": 2
        },
        ...
    ]
}
```


### User Routes
#### Register User
* Method : POST
* Route : `http://<HOST>:<PORT>/user`
* Sample Request
```
{
    "socialNetwork": "facebook",
    "accessToken": <string>,
    "accessSecret": "", //send as empty for facebook, required for twitter
}
```
* Sample Response :
```
{
    "userId": "547d52a7e4b008334921ff8c",
    "name": <string>,
    "url": "http://pbs.twimg.com/profile_images/504508299895517185/51LCx6sG.jpeg",
    "mobileNumber": "",
    "email": "singh25kamal@gmail.com",      
    "inviteCode": "f0kn2s",
    "verifiedFlag": false,
    "amountWon": 100,
    "completedChallenges": [
        "547bf054e4b008334921ff7e"
    ],
    "giftIds": [
        {
            "giftId": "547bf01be4b008334921ff7d",
            "remainingAmount": 100,
            "GiftStatus": "GIVEN"
        }
    ],
    "friendIds": []
}
```




#### Getting Friends
* Method : GET
* Route : `http://<HOST>:<PORT>/user/<userId>/friends`
Response
```
{
    "Friends": []
}
```

#### Getting User
* Method : GET
* Route : `http://<HOST>:<PORT>/user/<userId>`
* Sample Response
```
{
    "userId": "547d52a7e4b008334921ff8c",
    "name": <string>,
    "url": "http://pbs.twimg.com/profile_images/504508299895517185/51LCx6sG.jpeg",
    "mobileNumber": "",
    "email": "singh25kamal@gmail.com",      
    "inviteCode": "f0kn2s",
    "verifiedFlag": false,
    "amountWon": 100,
    "completedChallenges": [
        "547bf054e4b008334921ff7e"
    ],
    "giftIds": [
        {
            "giftId": "547bf01be4b008334921ff7d",
            "remainingAmount": 100,
            "GiftStatus": "GIVEN"
        }
    ],
    "friendIds": []
}
```

#### Get User Activity
* Method : GET
* Route : `http://<HOST>:<PORT>/user/<userId>/activity/<start>/<count>`
* Response
```
{
    "activities": [
        {
            "fromUser": "547d52a7e4b008334921ff8c",
            "activityId": "547db8e9e4b008334921ffac",
            "activityType": "LIKE",
            "description": "Sachin Gajraj liked Taco Bell Challenge",
            "likeCount": 0,
            "createTime": 1417525481351,
            "comments": [],
            "likedBy": []
        },
        ...
    ]
}


#### Get User Leaderboard
* Method : GET
* Route : `http://<HOST>:<PORT>/user/<userId>/leaderboard`
* Response
```
{
    "LeaderBoard": [
        {
            "userId": "547d52a7e4b008334921ff8c",
            "name": <string>,
            "url": "http://pbs.twimg.com/profile_images/504508299895517185/51LCx6sG.jpeg",
            "mobileNumber": "",
            "email": <string>,
            "inviteCode": "f0kn2s",
            "verifiedFlag": false,
            "amountWon": 100,
            "completedChallenges": [
                "547bf054e4b008334921ff7e"
            ],
            "giftIds": [
                {
                    "giftId": "547bf01be4b008334921ff7d",
                    "remainingAmount": 100,
                    "GiftStatus": "GIVEN"
                }
            ],
            "friendIds": []
        },
        ...
    ]
}


#### Update User Email ID
* Method : POST
* Route : `http://<HOST>:<PORT>/user/<userId>/email`
* Request
```
{
    "emailId" : <string>,
}
```
* Response
```
```



### Merchant Routes
#### Adding Merchant
* Method : POST
* Route : `http://<HOST>:<PORT>/merchant`
* Request
```
{
    "merchantName" : "test merchant",
    "logo": "test logo",
    "address" : "test address",
    "phone" : "12345678",
    "email" : "test email"
}
```
* Response
```
{
    "merchant": {
        "merchantId": "547da0c3e4b008334921ffa4",
        "name": "test merchant",
        "logoUrl": "test logo",
        "address": "test address",
        "phone": "12345678",
        "email": "test email",
        "Challenges": []
    }
}
```

#### Getting All Merchants
* Method : GET
* Route : `http://<HOST>:<PORT>/merchant`
* Response :
```
{
    "merchants": [
        {
            "merchantId": "5455dad43004245c22f1bfa6",
            "name": "test merchant1",
            "logoUrl": "test logo1",
            "address": "test address1",
            "phone": "12345678",
            "email": "test email1",
            "Challenges": []
        },
        ...
    ]
}  
```

#### Sync Merchants with Modo
* Method : GET
* Route : `http://<HOST>:<PORT>/modo/merchant/sync`
* Response
```
```

### Activity Routes
#### Comment on Activity
* Method : POST
* Route : `http://<HOST>:<PORT>/activity/<activityId>/comment`
* Request
```
{
     "text" : "le comment",
     "userId" : "547d52a7e4b008334921ff8c"
}
```
* Response
```
{
    "fromUser": "547d52a7e4b008334921ff8c",
    "activityId": "547da098e4b008334921ffa3",
    "activityType": "LIKE",
    "description": "Sachin liked Papa John's Challenge",
    "likeCount": 1,
    "createTime": 1417519256495,
    "comments": [
        {
            "userId": "547d52a7e4b008334921ff8c",
            "text": "le comment on activity",
            "time": 1417519434251
        },
        {
            "userId": "547d52a7e4b008334921ff8c",
            "text": "le comment",
            "time": 1417529381076
        }
    ],
    "likedBy": [
        "547d52a7e4b008334921ff8c"
    ]
}
```

#### Like an Activity
* Method : POST
* Route : `http://<HOST>:<PORT>/activity/<activityId>/comment`
* Request
```
{
     "userId" : "547d52a7e4b008334921ff8c"
}
```
* Response
```
{
    "fromUser": "547d52a7e4b008334921ff8c",
    "activityId": "547da098e4b008334921ffa3",
    "activityType": "LIKE",
    "description": "Sachin Gajraj liked Papa John's Challenge",
    "likeCount": 1,
    "createTime": 1417519256495,
    "comments": [
        {
            "userId": "547d52a7e4b008334921ff8c",
            "text": "le comment on activity",
            "time": 1417519434251
        },
        {
            "userId": "547d52a7e4b008334921ff8c",
            "text": "le comment",
            "time": 1417529381076
        }
    ],
    "likedBy": [
        "547d52a7e4b008334921ff8c"
    ]
}
```
