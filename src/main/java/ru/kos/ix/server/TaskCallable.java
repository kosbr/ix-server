package ru.kos.ix.server;

import ru.kos.ix.dto.AnsTask;
import ru.kos.ix.dto.Status;
import ru.kos.ix.dto.Task;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * Callable that represents task running. <Br/>
 * The main goal of this callable is to run task.methodName of task.serviceName with task.arguments <br/>
 * If all arguments are not null, it is possible to use standard
 * reflection getMethod(String name, Class<?>... parameterTypes) method.  <br/>
 * If some argument is null, it is impossible to detect its type, so the one way to find method is
 * browse all class methods with
 * such name and number of params and check if task's args are applicable to analysing method.
 * Created by Константин on 08.04.2016.
 */
public class TaskCallable implements Callable<AnsTask> {

    private final Task task;

    public TaskCallable(final Task task) {
        this.task = task;
    }

    @Override
    public AnsTask call() throws Exception {
        final ServiceHolder serviceHolder = ServiceHolder.getInstance();
        //try to find service
        final Object service = serviceHolder.getServiceByName(task.getServiceName());
        if (service == null) {
            throw new IllegalArgumentException("Service " + task.getServiceName() + " is not found");
        }
        //try to find method
        Method method = null;
        if (hasNullArgument(task.getArguments())) {
            method = findMethodByNameAndArgs(service.getClass(), task.getMethodName(), task.getArguments());
        } else {
            final Class<?>[] argumentsTypes = getArgumentsTypes(task.getArguments());
            method = service.getClass().getMethod(task.getMethodName(), argumentsTypes);
        }
        final Object result = method.invoke(service, task.getArguments());
        final boolean isVoid = method.getAnnotatedReturnType().getType() == Void.TYPE;
        return new AnsTask(task.getId(), result, isVoid ? Status.OK_VOID : Status.OK, null);
    }

    private boolean hasNullArgument(final Object[] arguments) {
        if (arguments == null) {
            return false;
        }
        for (Object argument : arguments) {
            if (argument == null) {
                return true;
            }
        }
        return false;
    }

    private Class<?>[] getArgumentsTypes(final Object[] arguments) {
        if (arguments == null) {
            return new Class<?>[0];
        }

        final Class<?>[] classes = new Class<?>[arguments.length];
        for (int i = 0; i < arguments.length; i++) {
            final Object argument = arguments[i];
            classes[i] = argument != null ? argument.getClass() : Object.class;
        }
        return classes;
    }

    private Method findMethodByNameAndArgs(final Class<?> serviceClass,
                                           final String name,
                                           final Object[] args) throws NoSuchMethodException {
        final Method[] declaredMethods = serviceClass.getDeclaredMethods();
        Method executableMethod = null;
        for (Method declaredMethod : declaredMethods) {
            if (declaredMethod.getName().equals(name)) {
                final boolean executable = canExecute(declaredMethod, args);
                if (executable) {
                    if (executableMethod != null) {
                        throw new NoSuchMethodException("There are more than one method "
                                + name + " are executable with such arguments");
                    }
                    executableMethod = declaredMethod;
                }
            }
        }
        if (executableMethod != null) {
            return executableMethod;
        } else {
            throw new NoSuchMethodException("Method " + name + " with such arguments is not found");
        }
    }

    private boolean canExecute(final Method method, final Object[] args) {
        final int parameterCount = method.getParameterCount();
        if (args == null) {
            return parameterCount == 0;
        }
        if (args.length != parameterCount) {
            return false;
        }
        final Class<?>[] parameterTypes = method.getParameterTypes();
        for (int i = 0; i < parameterCount; i++) {
            Object inputParameter = args[i];
            final Class<?> parameterType = parameterTypes[i];
            if (inputParameter != null && !parameterType.isInstance(inputParameter)) {
                return false;
            }
        }
        return true;
    }
}
