package com.example.mavenlambda;

import java.util.Map;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class App implements RequestHandler<Map<String, Object>, String> {
    private DynamoDB dynamoDB;
    private String tableName = "MyTable";

    @Override
    public String handleRequest(Map<String, Object> event, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("Received event: " + event);

        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
        this.dynamoDB = new DynamoDB(client);
        Table table = dynamoDB.getTable(tableName);

        String operation = null;
        String id = null;
        String name = null;

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode eventNode = objectMapper.valueToTree(event);

            if (eventNode.has("body")) {
                JsonNode bodyNode = objectMapper.readTree(eventNode.get("body").asText());
                operation = bodyNode.get("operation").asText();
                if (bodyNode.has("id")) {
                    id = bodyNode.get("id").asText();
                }
                if (bodyNode.has("name")) {
                    name = bodyNode.get("name").asText();
                }
            }
        } catch (Exception e) {
            logger.log("Error parsing event: " + e.getMessage());
            return "Error parsing event: " + e.getMessage();
        }

        String response;

        switch (operation) {
            case "create":
                response = createItem(id, name, table, logger);
                break;
            case "read":
                response = getItem(id, table, logger);
                break;
            case "update":
                response = updateItem(id, name, table, logger);
                break;
            case "delete":
                response = deleteItem(id, table, logger);
                break;
            default:
                response = "Unknown operation: " + operation;
        }

        return response;
    }

    private String createItem(String id, String name, Table table, LambdaLogger logger) {
        logger.log("Creating item with id: " + id + " and name: " + name);

        if (id != null && name != null) {
            Item item = new Item().withPrimaryKey("id", id).withString("name", name);
            table.putItem(item);
            logger.log("Item inserted: " + item.toJSON());
            return "Item inserted successfully";
        } else {
            logger.log("Missing parameters: id or name");
            return "Missing parameters: id or name";
        }
    }

    private String getItem(String id, Table table, LambdaLogger logger) {
        logger.log("Getting item with id: " + id);

        if (id != null) {
            Item item = table.getItem("id", id);
            if (item != null) {
                logger.log("Item retrieved: " + item.toJSON());
                return item.toJSON();
            } else {
                logger.log("Item not found with id: " + id);
                return "Item not found";
            }
        } else {
            logger.log("Missing parameter: id");
            return "Missing parameter: id";
        }
    }

    private String updateItem(String id, String name, Table table, LambdaLogger logger) {
        logger.log("Updating item with id: " + id + " and name: " + name);

        if (id != null && name != null) {
            try {
                UpdateItemSpec updateItemSpec = new UpdateItemSpec()
                    .withPrimaryKey("id", id)
                    .withUpdateExpression("set #nm = :name")
                    .withNameMap(new NameMap().with("#nm", "name"))
                    .withValueMap(new ValueMap().withString(":name", name));

                UpdateItemOutcome outcome = table.updateItem(updateItemSpec);
                logger.log("Item updated: " + outcome.getItem().toJSON());
                return "Item updated successfully";
            } catch (Exception e) {
                logger.log("Exception during update: " + e.getMessage());
                return "Error during update: " + e.getMessage();
            }
        } else {
            logger.log("Missing parameters: id or name");
            return "Missing parameters: id or name";
        }
    }

    private String deleteItem(String id, Table table, LambdaLogger logger) {
        logger.log("Deleting item with id: " + id);

        if (id != null) {
            table.deleteItem("id", id);
            logger.log("Item deleted with id: " + id);
            return "Item deleted successfully";
        } else {
            logger.log("Missing parameter: id");
            return "Missing parameter: id";
        }
    }
}
