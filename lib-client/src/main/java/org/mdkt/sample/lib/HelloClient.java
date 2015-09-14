package org.mdkt.sample.lib;

import com.netflix.client.config.CommonClientConfigKey;
import com.netflix.client.config.DefaultClientConfigImpl;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.LoadBalancerBuilder;

import feign.Feign;
import feign.Headers;
import feign.RequestLine;
import feign.ribbon.LBClient;
import feign.ribbon.LBClientFactory;
import feign.ribbon.RibbonClient;

public interface HelloClient {
	@RequestLine("GET /hello")
	@Headers(value={
			"Accept: application/json",
			"Content-type: application/json"
	})
	String hello();
	
	public static class Builder {
		private static final String CLIENT_NAME = "helloClient";
		
		private DefaultClientConfigImpl clientConfig = new DefaultClientConfigImpl();
		private boolean enableSsl = true;

		private Builder() {
			clientConfig.loadDefaultValues();
			clientConfig.setClientName(CLIENT_NAME);
		}
		
		public static Builder newBuilder() {
			return new Builder();
		}
		
		public Builder enableSsl(boolean ssl) {
			this.enableSsl = ssl;
			return this;
		}
		
		public Builder servers(String ...servers) {
			StringBuffer listOfServers = new StringBuffer();
			for (String s : servers) {
				listOfServers.append(",").append(s);
			}
			if (listOfServers.length() > 0) {
				listOfServers.deleteCharAt(0);
				clientConfig.set(CommonClientConfigKey.ListOfServers, listOfServers.toString());
			}
			return this;
		}
		
		public HelloClient build() {
			final ILoadBalancer lb = LoadBalancerBuilder.newBuilder().withClientConfig(clientConfig).buildLoadBalancerFromConfigWithReflection();
			LBClientFactory lbClientFactory = new LBClientFactory() {
				@Override
				public LBClient create(String clientName) {
					return LBClient.create(lb, clientConfig);
				}
			};
			
			return Feign.builder().client(RibbonClient.builder().lbClientFactory(lbClientFactory).build()).target(HelloClient.class, (enableSsl ? "https://" : "http://") + CLIENT_NAME);
		}
	}
}
