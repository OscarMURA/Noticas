package co.icesi.taskManager.services.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.icesi.taskManager.model.Task;
import co.icesi.taskManager.model.User;
import co.icesi.taskManager.repositories.TaskRepository;
import co.icesi.taskManager.repositories.UserRepository;
import co.icesi.taskManager.services.interfaces.TaskService;

@Service
public class TaskServiceImp implements TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    @Override
    public Task updateTask(Task task) {
        return taskRepository.save(task);
    }

    @Override
    public void deleteTask(long taskId) {
        taskRepository.deleteById(taskId);
    }

    @Override
    public void assignTask(long taskId, long userId) {
        Task taskToAssignUser = taskRepository.findById(taskId).get();
        User user = userRepository.findById(userId).get();

        user.getTasks().add(taskToAssignUser);
        taskToAssignUser.getAssignedUsers().add(user);

        taskRepository.save(taskToAssignUser);
        userRepository.save(user);
    }

    @Override
    public void unassignTask(long taskId, long userId) {
        Task taskToUnAssignUser = taskRepository.findById(taskId).get();
        User user = userRepository.findById(userId).get();

        List<User> assignedUser = taskToUnAssignUser.getAssignedUsers();
        List<Task> assignTask = user.getTasks();

        //TODO: FINISH IF TIME
    }

    @Override
    public Task getTaskById(long taskId) {
        Optional<Task> taskToGet = taskRepository.findById(taskId);
        if (taskToGet.isEmpty()){
            return null;
        }
        return taskRepository.findById(taskId).get();
    }

    @Override
    public List<Task> getAllTask() {
        return taskRepository.findAll();
    }

}
