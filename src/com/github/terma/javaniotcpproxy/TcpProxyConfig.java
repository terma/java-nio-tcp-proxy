package com.github.terma.javaniotcpproxy;

public interface TcpProxyConfig {
  int getLocalPort();

  int getRemotePort();

  String getRemoteHost();

  int getWorkerCount();

  void setWorkerCount(int workerCount);
}
