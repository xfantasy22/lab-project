package ru.itmo.service.executor;

import ru.itmo.context.Context;
import ru.itmo.model.State;
import ru.itmo.service.holder.CommandHolder;
import ru.itmo.util.ParseUtils;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class CommandExecutorImpl implements CommandExecutor {

    @Override
    public Supplier<State> execute(String unparsedCommand) {

        List<String> list = ParseUtils.parseStringToTwoValues(unparsedCommand);

        if (State.FAILED == validateCommands(list)) {
            return () -> State.FAILED;
        }

        return () -> execute(list);
    }

    private State execute(List<String> commands) {
            Function<String, State> state = Context.getContext().getCommandHolder().command(commands.get(0));
            if (commands.size() == 1) {
                return state.apply(null);
            }
            return state.apply(commands.get(1));
    }

    private State validateCommands(List<String> commands) {
        if (commands.isEmpty()) {
            System.out.println("Command cannot be empty");
            return State.FAILED;
        }
        return State.RUN;
    }
}
