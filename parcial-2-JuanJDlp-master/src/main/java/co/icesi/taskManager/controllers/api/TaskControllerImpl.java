package co.icesi.taskManager.controllers.api;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.icesi.taskManager.dtos.TaskDto;
import co.icesi.taskManager.mappers.TaskMapper;
import co.icesi.taskManager.model.Task;
import co.icesi.taskManager.services.interfaces.TaskService;

@RestController
@RequestMapping("/tasks")
public class TaskControllerImpl implements TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskMapper taskMapper;

    @GetMapping
    public ResponseEntity<?> findAllTask() {
        List<TaskDto> tasksDto = taskService.getAllTask().stream()
                .map(taskMapper::taskToTaskDto)
                .collect(Collectors.toList());
        ;
        return ResponseEntity.ok().body(tasksDto);
    }

    @PostMapping()
    public ResponseEntity<?> addTask(@RequestBody TaskDto dto) {
        Task taskToSave = taskMapper.taskDtoToTask(dto);
        return ResponseEntity.status(201).body(taskService.createTask(taskToSave));
    }

    @PutMapping
    public ResponseEntity<?> updateTask(@RequestBody TaskDto dto) {
        Task taskToUpdate = taskMapper.taskDtoToTask(dto);
        return ResponseEntity.ok().body(taskService.updateTask(taskToUpdate));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable long id) {
        Task taskToDelete = taskService.getTaskById(id);
        if (taskToDelete == null) {
            return ResponseEntity.status(404).build();
        }
        taskService.deleteTask(id);
        return ResponseEntity.status(200).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable long id) {
        Task taskToGet = taskService.getTaskById(id);

        if (taskToGet == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok().body(taskMapper.taskToTaskDto(taskToGet));
    }

}
