output "ecs_service_name" {
  value = aws_ecs_service.lagoinha_backend_service.name
}

output "ecs_cluster_name" {
  value = aws_ecs_cluster.lagoinha_cluster.name
}
