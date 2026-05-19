package server;

import com.example.grpc.CalculatorGrpc;
import com.example.grpc.Request;
import com.example.grpc.Response;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;

public class CalculatorServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(50051)
                .addService(new CalculatorService())
                .build();

        System.out.println("Servidor gRPC iniciado en el puerto 50051...");
        server.start();
        server.awaitTermination();
    }

    static class CalculatorService extends CalculatorGrpc.CalculatorImplBase {
        @Override
        public void sum(Request req, StreamObserver<Response> responseObserver) {
            int result = req.getA() + req.getB();
            Response response = Response.newBuilder()
                    .setResult(result)
                    .build();

            System.out.println("Recibido: " + req.getA() + " + " + req.getB() + " = " + result);

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}
