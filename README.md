# First Aid Manual

Interactive first aid manual Android application.

## To run

- Clone the respository
- Open on Android Studio and run.

## Development

For efficiency, FAmanual is calls different API endpoints (`/ask1`, `/ask2`, `/ask4`, `/ask5`) based on the user input. More details on this can be found in `SpeechActivity.java`.

FAmanual utilizes Bing Image Search API to render images for "definition" questions. In order to continue using the API, you will need a subscription key from Microsoft.