package main

import (
	"context"
	"log"
	"os"
	"time"

	"converter_grpc/pb"

	"github.com/joho/godotenv"
	"google.golang.org/grpc"
	"google.golang.org/grpc/credentials/insecure"
)

func main() {
	_ = godotenv.Load()
	addr := os.Getenv("GRPC_SERVER_ADDR")
	if addr == "" { addr = "localhost:50052"}
	conn, err := grpc.Dial(addr, grpc.WithTransportCredentials(insecure.NewCredentials()))
	if err != nil {
		log.Fatalf("did not connect: %v", err)
	}
	defer conn.Close()
	c := pb.NewConverterClient(conn)

	ctx, cancel := context.WithTimeout(context.Background(), time.Second)
	defer cancel()

	// Prueba 1: Celsius a Fahrenheit
	r, err := c.ConvertCelsiusToFahrenheit(ctx, &pb.ConvertRequest{Value: 25})
	if err != nil {
		log.Fatalf("could not convert: %v", err)
	}
	log.Printf("25°C = %f°F", r.GetResult())

	// Prueba 2: Soles a Dólares
	r, err = c.ConvertSolesToDollars(ctx, &pb.ConvertRequest{Value: 100})
	if err != nil {
		log.Printf("Error: %v", err)
	} else {
		log.Printf("100 Soles = %f Dólares", r.GetResult())
	}

	// Prueba 3: Kilómetros a Millas
	r, err = c.ConvertKmToMiles(ctx, &pb.ConvertRequest{Value: 10})
	if err != nil {
		log.Printf("Error: %v", err)
	} else {
		log.Printf("10 Km = %f Millas", r.GetResult())
	}

	// Prueba 4: Validación (valor negativo)
	r, err = c.ConvertKmToMiles(ctx, &pb.ConvertRequest{Value: -5})
	if err != nil {
		log.Printf("Validación correcta: %v", err)
	} else {
		log.Printf("Error: se esperaba error de validación")
	}
}
