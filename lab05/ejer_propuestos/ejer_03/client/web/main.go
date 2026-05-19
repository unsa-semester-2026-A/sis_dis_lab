package main

import (
	"context"
	"encoding/json"
	"log"
	"net/http"
	"os"
	"time"

	"converter_grpc/pb"

	"github.com/joho/godotenv"
	"google.golang.org/grpc"
	"google.golang.org/grpc/credentials/insecure"
)

type ConvertRequest struct {
	Type  string  `json:"type"`
	Value float64 `json:"value"`
}

type ConvertResponse struct {
	Result float64 `json:"result"`
	Error  string  `json:"error,omitempty"`
}

var client pb.ConverterClient

func main() {
	// Cargar .env si existe
	_ = godotenv.Load()

	grpcAddr := os.Getenv("GRPC_SERVER_ADDR")
	if grpcAddr == "" {
		grpcAddr = "localhost:50052"
	}

	webPort := os.Getenv("WEB_PORT")
	if webPort == "" {
		webPort = "8080"
	}

	// Conectar al servidor gRPC
	conn, err := grpc.Dial(grpcAddr, grpc.WithTransportCredentials(insecure.NewCredentials()))
	if err != nil {
		log.Fatalf("No se pudo conectar con gRPC: %v", err)
	}
	defer conn.Close()
	client = pb.NewConverterClient(conn)

	// Rutas
	http.Handle("/", http.FileServer(http.Dir("./web")))
	http.HandleFunc("/api/convert", handleConvert)

	log.Printf("Servidor Web iniciado en http://localhost:%s", webPort)
	if err := http.ListenAndServe(":"+webPort, nil); err != nil {
		log.Fatalf("Error al iniciar servidor web: %v", err)
	}
}

func handleConvert(w http.ResponseWriter, r *http.Request) {
	if r.Method != http.MethodPost {
		http.Error(w, "Método no permitido", http.StatusMethodNotAllowed)
		return
	}

	var req ConvertRequest
	if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
		http.Error(w, err.Error(), http.StatusBadRequest)
		return
	}

	ctx, cancel := context.WithTimeout(context.Background(), time.Second*5)
	defer cancel()

	var result *pb.ConvertResponse
	var err error

	// Llamada gRPC según el tipo
	switch req.Type {
	case "celsius":
		result, err = client.ConvertCelsiusToFahrenheit(ctx, &pb.ConvertRequest{Value: req.Value})
	case "soles":
		result, err = client.ConvertSolesToDollars(ctx, &pb.ConvertRequest{Value: req.Value})
	case "km":
		result, err = client.ConvertKmToMiles(ctx, &pb.ConvertRequest{Value: req.Value})
	default:
		http.Error(w, "Tipo de conversión inválido", http.StatusBadRequest)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	if err != nil {
		w.WriteHeader(http.StatusInternalServerError)
		json.NewEncoder(w).Encode(ConvertResponse{Error: err.Error()})
		return
	}

	json.NewEncoder(w).Encode(ConvertResponse{Result: result.GetResult()})
}
