## Setting Up AWS Lambda with DynamoDB Using Java and API Gateway

### Prerequisites
- macOS with Homebrew installed
- AWS Account

### Steps

#### 1. Install Maven
```sh
brew install maven
```

#### 2. Create a Maven Project
1. Open Visual Studio Code (VS Code).
2. Install the "Java Extension Pack" in VS Code.
3. Create a new Maven project:
   - Open the Command Palette (`Cmd + Shift + P`).
   - Select `Maven: Generate from Archetype`.
   - Choose `maven-archetype-quickstart`.
   - Follow the prompts to set the group ID, artifact ID, and package.

#### 3. Update `pom.xml`
Add dependencies for AWS SDK and build plugins.

#### 4. Write Lambda Function (`App.java`)
Create `src/main/java/com/example/mavenlambda/App.java` and implement the Lambda handler logic.

#### 5. Package the Lambda Function
```sh
mvn clean package
```

#### 6. Create and Configure Lambda Function
1. **Create Lambda Function:**
   - Go to AWS Lambda Console.
   - Click `Create function`, choose `Author from scratch`, and set the runtime to `Java 11`.
   - Upload the generated JAR file (`target/your-artifact-id-1.0-SNAPSHOT-shaded.jar`).

2. **Set Handler:**
   - Set the handler to `com.example.mavenlambda.App::handleRequest`.

3. **Attach Policies:**
   - Attach `AWSLambdaBasicExecutionRole` and `AmazonDynamoDBFullAccess` policies to the Lambda execution role.

#### 7. Create DynamoDB Table
1. **Go to DynamoDB Console:**
   - Create a new table named `MyTable` with `id` as the primary key.

#### 8. Create and Configure API Gateway
1. **Create API:**
   - Go to API Gateway Console.
   - Create a new REST API.

2. **Create Resource and Methods:**
   - Create a new resource (`/items`).
   - Create a `POST` method, set the integration type to Lambda, and link to your Lambda function.

3. **Enable CORS:**
   - Enable CORS for the resource.

4. **Deploy API:**
   - Create a new stage (e.g., `dev`) and deploy the API.

#### 9. Enable Logging in API Gateway
1. **Configure CloudWatch Logs:**
   - Go to `Stages`, select your stage (`dev`), and enable `CloudWatch Logs` with `INFO` log level.
   - Set the log destination to the ARN of your CloudWatch Log Group.

#### 10. Test API with Postman
1. **Open Postman:**
   - Create a new `POST` request to `https://tfz9ef1xk9.execute-api.us-east-1.amazonaws.com/dev/items`.
   - Set headers: `Content-Type: application/json`.
   - Set body:
     ```json
     {
       "operation": "create",
       "id": "123",
       "name": "John Doe"
     }
     ```
   - Send the request and verify the response.

### Conclusion
By following these steps, I set up a Java-based AWS Lambda function with DynamoDB and API Gateway, and tested it using Postman. I also ensured to check CloudWatch Logs for any debugging information.
