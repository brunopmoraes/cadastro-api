provider "aws" {
  region = "us-east-1" # Mude para a sua região
}

resource "aws_ecs_cluster" "lagoinha_cluster" {
  name = "ecs-lagoinha-cluster"
}

resource "aws_ecs_task_definition" "lagoinha_backend_task" {
  family                   = "ecs-lagoinha-backend-task"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "256"
  memory                   = "512"

  container_definitions = jsonencode([
    {
      name        = "cadastro-api"
      image       = "881490088770.dkr.ecr.us-east-1.amazonaws.com/lagoinha/cadastro-api:latest" # Coloque aqui o URL da imagem do ECR
      essential   = true
      portMappings = [
        {
          containerPort = 80
          hostPort      = 80
        }
      ]
      environment = [
        {
          name  = "DYNAMODB_TABLE_CADASTRO"
          value = "Cadastro"
        },
        {
          name  = "DYNAMODB_TABLE_PRESENCA"
          value = "Presenca"
        }
      ]
    }
  ])

  execution_role_arn = aws_iam_role.ecs_task_execution_role.arn
  task_role_arn      = aws_iam_role.ecs_task_execution_role.arn
}

resource "aws_ecs_service" "lagoinha_backend_service" {
  name            = "ecs-lagoinha-backend-service"
  cluster         = aws_ecs_cluster.lagoinha_cluster.id
  task_definition = aws_ecs_task_definition.lagoinha_backend_task.arn
  desired_count   = 1
  launch_type     = "FARGATE"

  network_configuration {
    subnets         = ["subnet-0928cacd783a06c81", "subnet-0b6978ce494f11f09", "subnet-0c5323c9750673c63", "subnet-010f51e1169aa3edb", "subnet-0f1a6ef053ad52bb8", "subnet-0e0c7e065000d5f9a"] # Subnets da sua VPC
    security_groups = [aws_security_group.ecs_service_sg.id]
    assign_public_ip = true
  }
}

resource "aws_security_group" "ecs_service_sg" {
  name_prefix = "ecs-lagoinha-backend-sg"

  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_iam_role" "ecs_task_execution_role" {
  name = "ecs_task_execution_role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "ecs-tasks.amazonaws.com"
        }
      }
    ]
  })

  managed_policy_arns = [
    "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
  ]
}

resource "aws_iam_role_policy" "ecs_dynamodb_access" {
  name   = "ecs_dynamodb_access_policy"
  role   = aws_iam_role.ecs_task_execution_role.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = [
          "dynamodb:GetItem",
          "dynamodb:PutItem",
          "dynamodb:Query",
          "dynamodb:UpdateItem"
        ]
        Effect   = "Allow"
        Resource = [
          "arn:aws:dynamodb:us-east-1:881490088770:table/Cadastro",
          "arn:aws:dynamodb:us-east-1:881490088770:table/Presenca"
        ]
      }
    ]
  })
}
