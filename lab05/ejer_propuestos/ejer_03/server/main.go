package main

import (
	"context"
	"fmt"
	"log"
	"net"
	"os"

	"converter_grpc/pb"

	"github.com/joho/godotenv"
	"google.golang.org/grpc"
	"google.golang.org/grpc/codes"
	"google.golang.org/grpc/status"
)

type server struct {
	pb.UnimplementedConverterServer
}

func (s *server) ConvertCelsiusToFahrenheit(ctx context.Context, req *pb.ConvertRequest) (*pb.ConvertResponse, error) {
	log.Printf("Petición: Celsius a Fahrenheit, Valor: %f", req.GetValue())
	result := req.GetValue()*1.8 + 32
	return &pb.ConvertResponse{Result: result}, nil
}

func (s *server) ConvertSolesToDollars(ctx context.Context, req *pb.ConvertRequest) (*pb.ConvertResponse, error) {
	log.Printf("Petición: Soles a Dólares, Valor: %f", req.GetValue())
	if req.GetValue() < 0 {
		return nil, status.Errorf(codes.InvalidArgument, "El valor no puede ser negativo")
	}
	// Tasa de cambio aproximada
	result := req.GetValue() / 3.75
	return &pb.ConvertResponse{Result: result}, nil
}

func (s *server) ConvertKmToMiles(ctx context.Context, req *pb.ConvertRequest) (*pb.ConvertResponse, error) {
	log.Printf("Petición: Kilómetros a Millas, Valor: %f", req.GetValue())
	if req.GetValue() < 0 {
		return nil, status.Errorf(codes.InvalidArgument, "La distancia no puede ser negativa")
	}
	result := req.GetValue() * 0.621371
	return &pb.ConvertResponse{Result: result}, nil
}

func main() {
	_ = godotenv.Load()
	port:= os.Getenv("SERVER_PORT")
	if port == "" {
		port = "50052"
	}
	lis, err := net.Listen("tcp", ":" + port)
	if err != nil {
		log.Fatalf("failed to listen: %v", err)
	}
	s := grpc.NewServer()
	pb.RegisterConverterServer(s, &server{})
	fmt.Println("Servidor Go gRPC iniciado en el puerto 50052...")
	if err := s.Serve(lis); err != nil {
		log.Fatalf("failed to serve: %v", err)
	}
}
