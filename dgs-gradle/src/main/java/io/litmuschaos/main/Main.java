package io.litmuschaos.main;

import com.netflix.graphql.dgs.client.*;
import com.netflix.graphql.dgs.client.codegen.GraphQLQueryRequest;
import io.litmuschaos.client.GetChaosHubGraphQLQuery;
import io.litmuschaos.client.GetChaosHubProjectionRoot;
import io.litmuschaos.client.ListChaosHubGraphQLQuery;
import io.litmuschaos.client.ListChaosHubProjectionRoot;
import okhttp3.*;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        OkHttpClient okHttpClient = new OkHttpClient();
        String host = "http://localhost:54160/api/query";

        CustomGraphQLClient client = GraphQLClient.createCustom(host, (url, headers, body) -> {
            Request.Builder requestBuilder = new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(MediaType.parse("application/json"), body));

            headers.forEach((key, values) -> values.forEach(value -> requestBuilder.addHeader(key, value)));
            requestBuilder.addHeader("Authorization", "Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE3MzUxOTMxNTIsInJvbGUiOiJhZG1pbiIsInVpZCI6ImMxMmYzOTAxLWQ2MDMtNGU5OS1hMWYyLWNmZGNiNDUyZTQ5YSIsInVzZXJuYW1lIjoiYWRtaW4ifQ.Plu3dMblE3d3I6-18DokgJCaUm966nW31fh3DOA_hvIpD1uJ8bFWZrtxLkpxY1mqy46t_VJtBSai913dZ_pjug");

            try (Response response = okHttpClient.newCall(requestBuilder.build()).execute()) {
                return new HttpResponse(response.code(), response.body().string());
            } catch (IOException e) {
                throw new RuntimeException("Error executing GraphQL query", e);
            }
        });

        GraphQLQueryRequest graphQLQueryRequest =
                new GraphQLQueryRequest(
                        new ListChaosHubGraphQLQuery.Builder()
                                .projectID("8d2dc452-00dc-4ff9-968f-b8105385ecdb")
                                .build(),
                        new ListChaosHubProjectionRoot<>()
                                .name()
                        );

        String query = graphQLQueryRequest.serialize();
        GraphQLResponse graphQLResponse = client.executeQuery(query);
        String result = graphQLResponse.getJson();
        System.out.println("result: " + result);
    }
}
