# Proyecto de curso: Icesi Trade

## Integrantes:
- Sara Lucia Diaz Puerta
- Sebastian Erazo Ochoa
- Oscar Muñoz Ramirez

## Propuesta de modelo.
[Modelo relacional](docs/MR-IcesiTrade.pdf)

## Compilar y Ejecutar el Proyecto

Para compilar y ejecutar la aplicación, usa el siguiente comando:
```sh
mvn clean install
mvn spring-boot:run
```

## Ejecución de Pruebas con Reporte JaCoCo

Para ejecutar las pruebas y generar el reporte de cobertura con JaCoCo, utiliza:
```sh
mvn clean test
```

Para generar el reporte de cobertura en HTML:
```sh
mvn jacoco:report
```

El informe se generará en:
```
target/site/jacoco/index.html
```

Puedes abrirlo en un navegador para revisar la cobertura de código.

Los servicios JwtService y JwtAuthenticationFilter no tienen tests ya que no hacen parte de esta entrega.

# Para probar la aplicación desplegada

Visitar: 
http://10.147.19.21:8080/g1/losbandalos/

Usuario admin:
- juan.perez@example.com Password: 1234

Manual de uso:
https://www.youtube.com/watch?v=tsSkeuL3_y8

# VIDEO PRUEBAS POSTMAN: 
https://drive.google.com/file/d/15BhOAzbI6BS4Aa5M5-sNW92twxssAVIq/view

## Para correr postman debes importar en collection el archivo: 
Postman\IcesiTrade -  API.postman_collection.json

![image](https://github.com/user-attachments/assets/474a1686-9f80-49d5-bc23-e8dd2026da0d)

![image](https://github.com/user-attachments/assets/5dc8f027-b673-46a0-b530-78602142dcc3)


## E importart las variables de env: 
Postman\IcesiTrade Environment.postman_environment.json

![image](https://github.com/user-attachments/assets/de0b7ae9-c187-4beb-a1a3-0df09c68e633)

 ! Debes tener activos las variables activadas con el chulito


## En variables de env debes agregar la ruta del proyecto. Para initial y current value. Y guardar

![image](https://github.com/user-attachments/assets/21616c55-030a-4955-9039-846371932a04)


Direccion despliegue: http://10.147.19.21:8080/g1/losbandalos
Direccion local: http://localhost:8080/g1/losbandalos

(Sin importar donde los despliegues, esta conectado a la misma base de datos postgres) 

## Para correr las pruebas debes hacer lo siguiente:

- Presionar a los tres punticos:

![image](https://github.com/user-attachments/assets/7398f873-1af9-4e72-8581-b3e014456a75)

- Luego presionar run:

![image](https://github.com/user-attachments/assets/4df95709-af4e-4661-aff1-f6f42257b1d4)

- Para al final presionar Run Icesi Trade Api

![image](https://github.com/user-attachments/assets/4fa5296c-1f74-48a6-bb33-3c0dd0d002b9)


# SIEMPRE CONTROL + S. PARA GUARDAR CAMBIOS EN POSTMAN. NOS LO AGRADECERA



 




