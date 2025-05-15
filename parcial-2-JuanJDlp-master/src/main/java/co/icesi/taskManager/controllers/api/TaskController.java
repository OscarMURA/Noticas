package co.icesi.taskManager.controllers.api;

import org.springframework.http.ResponseEntity;

import co.icesi.taskManager.dtos.TaskDto;

public interface TaskController {

    public ResponseEntity<?> findAllTask();

    public ResponseEntity<?> addTask(TaskDto dto);

    public ResponseEntity<?> updateTask(TaskDto dto);

    public ResponseEntity<?> deleteTask(long id);

    public ResponseEntity<?> findById(long id);

}
