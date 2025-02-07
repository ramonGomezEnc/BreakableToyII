# BreakableToyII

## Features
- Search for flights using the Amadeus API
- Straightforward user interface
- Fully containerized with Docker

## Prerequisites
- Docker must be installed.
- Obtain Amadeus API credentials (Client ID and Client Secret) from the Amadeus Developer Portal.

## Getting Started
1. Clone the Repository
git clone https://github.com/ramonGomezEnc/BreakableToyII.git
cd BreakableToyII
2. Create the .env File
Inside the backend folder, create a .env file with:
API_CLIENT_KEY=your_amadeus_api_key
API_CLIENT_SECRET=your_amadeus_api_secret
Replace your_amadeus_api_key and your_amadeus_api_secret with your actual credentials.
3. Run the Application
Build and start the containers:
docker-compose up --build
4. Access the app at:
http://localhost:8080

## Usage
1. Open your browser and go to http://localhost:8080.
2. Fill out the flight search form and click search.
3. Select a specific flight option if you want to find more details about it.

## Troubleshooting
- Make sure Docker is running.
- Confirm that your .env file has the correct Amadeus credentials.
- Known bug: The first time you search from the landing page, you may need to refresh the page—this only happens once.