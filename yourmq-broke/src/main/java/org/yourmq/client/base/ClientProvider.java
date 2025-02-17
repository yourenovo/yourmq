package org.yourmq.client.base;



public interface ClientProvider {
    String[] schemas();

    Client createClient(ClientConfig clientConfig);
}