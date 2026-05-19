FROM golang:alpine

# Install protoc and dependencies
RUN apk add --no-cache protobuf-dev protoc

# Install Go gRPC plugins
RUN go install google.golang.org/protobuf/cmd/protoc-gen-go@v1.33 && \
    go install google.golang.org/grpc/cmd/protoc-gen-go-grpc@v1.3

WORKDIR /app

COPY . .

# Generate gRPC code
RUN protoc --go_out=. --go-grpc_out=. proto/converter.proto

# Download dependencies
RUN go mod download

# Build the server and client
RUN go build -o server_bin server/main.go && \
    go build -o client_console_bin client/console/main.go && \
    go build -o client_web_bin client/web/main.go

CMD ["./server_bin"]
