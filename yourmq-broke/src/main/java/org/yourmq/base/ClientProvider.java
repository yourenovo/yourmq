package org.yourmq.base;



public interface ClientProvider {
    String[] schemas();

    Client createClient(ClientConfig clientConfig);
}