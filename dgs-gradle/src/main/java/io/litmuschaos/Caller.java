package io.litmuschaos;

import com.jayway.jsonpath.TypeRef;
import com.netflix.graphql.dgs.client.CustomGraphQLClient;
import com.netflix.graphql.dgs.client.GraphQLClient;
import com.netflix.graphql.dgs.client.GraphQLResponse;
import com.netflix.graphql.dgs.client.HttpResponse;
import com.netflix.graphql.dgs.client.codegen.BaseProjectionNode;
import com.netflix.graphql.dgs.client.codegen.GraphQLQueryRequest;
import io.litmuschaos.generated.client.ListChaosHubGraphQLQuery;
import io.litmuschaos.generated.client.ListChaosHubProjectionRoot;
import io.litmuschaos.generated.client.ListInfrasGraphQLQuery;
import io.litmuschaos.generated.client.ListInfrasProjectionRoot;
import io.litmuschaos.generated.types.ChaosHubStatus;
import io.litmuschaos.generated.types.ListInfraResponse;
import okhttp3.*;

import java.io.IOException;
import java.util.List;

public class Caller {

    public CustomGraphQLClient client;

    public Caller(){
        OkHttpClient okHttpClient = new OkHttpClient();
        String host = "http://localhost:65416/api/query";

        this.client = GraphQLClient.createCustom(host, (url, headers, body) -> {
            Request.Builder requestBuilder = new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(MediaType.parse("application/json"), body));

            headers.forEach((key, values) -> values.forEach(value -> requestBuilder.addHeader(key, value)));
            requestBuilder.addHeader("Authorization", "Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE3MzYwNjM4MTYsInJvbGUiOiJhZG1pbiIsInVpZCI6ImI4YTVkYWY3LTgyMDItNDc4YS1hODhjLWM3MmJlNWUyMDk5NyIsInVzZXJuYW1lIjoiYWRtaW4ifQ.YI72vEsIY2R3UT3a80oKGDB7REw242GNYJUQfHna0WihDgxPCWYl460NamzJ_vvIw1zPu2Vg40r3yyV7Km6E1g");

            try (Response response = okHttpClient.newCall(requestBuilder.build()).execute()) {
                return new HttpResponse(response.code(), response.body().string());
            } catch (IOException e) {
                throw new RuntimeException("Error executing GraphQL query", e);
            }
        });
    }

    public void Test(){
        ListInfraResponse r = listInfras(
                new ListInfrasGraphQLQuery
                        .Builder()
                        .projectID("8d2dc452-00dc-4ff9-968f-b8105385ecdb")
                        .build(),
                new ListInfrasProjectionRoot<>()
                        .infras()
                            .infraID()
                            .name()
                            .parent()
                        .totalNoOfInfras()

        );

        System.out.println(r);
    }

    public ListInfraResponse listInfras(ListInfrasGraphQLQuery query, ListInfrasProjectionRoot projection){
        String request = new GraphQLQueryRequest(query, projection).serialize();
        GraphQLResponse response = client.executeQuery(request);
        return response.dataAsObject(ListInfraResponse.class);
    }

    public List<ChaosHubStatus> listChaosHub(ListChaosHubGraphQLQuery query, ListChaosHubProjectionRoot projection){
        String request = new GraphQLQueryRequest(query, projection).serialize();
        GraphQLResponse response = client.executeQuery(request);
        return response.extractValueAsObject("data.listChaosHub[*]", new TypeRef<List<ChaosHubStatus>>() {});
    }
}
