package net.splodgebox.cosmicfunds.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class FundGoal {

    private final String name;
    private final long cost;
    private final List<String> blockedCommands;
    private final List<String> rewardCommands;
    private final String message;
    private final List<String> completeMessage;

}
