package client;

import com.example.grpc.CalculatorGrpc;
import com.example.grpc.Request;
import com.example.grpc.Response;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class CalculatorClient {
  public static void main(String[] args) {
    String host = System.getenv("SERVER_HOST");
    if (host == null || host.isEmpty()) {
      host = "localhost";
    }
    String portStr = System.getenv("SERVER_PORT");
    int port = (portStr != null && !portStr.isEmpty()) ? Integer.parseInt(portStr):50051;
    System.out.println("Conectando al servidor en: " + host + ":"+ port);
    ManagedChannel channel = ManagedChannelBuilder.forAddress(host,port)
    .usePlaintext()
    .build();

    CalculatorGrpc.CalculatorBlockingStub stub = CalculatorGrpc.newBlockingStub(channel);

    Request request = Request.newBuilder()
    .setA(8)
    .setB(4)
    .build();

    System.out.println("Enviando petición: 8 + 4");
    Response response = stub.sum(request);

    System.out.println("Resultado: " + response.getResult());

    channel.shutdown();
  }
}
