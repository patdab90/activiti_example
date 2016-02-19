package pl.jcommerce.activiti_test.foodorderer.service;

import org.activiti.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

@Component
public class VoteService {

	public void resetVotes(DelegateExecution execution) {
		execution.setVariable("votesFor", Integer.valueOf(0));
	}

	public void incrementNumberOfVotes(DelegateExecution execution) {
		synchronized (this) {
			Integer numberOfVotes = defaultIfNull((Integer) execution.getVariable("numberOfVotes"));
			execution.setVariable("numberOfVotes", numberOfVotes + 1);
		}
	}

	public void vote(DelegateExecution execution, boolean vote) {
		synchronized (this) {
			Integer votesFor = defaultIfNull((Integer) execution.getVariable("votesFor"));
			if (vote) {
				execution.setVariable("votesFor", votesFor + 1);
			}
		}
	}

	private Integer defaultIfNull(final Integer number) {
		return number == null ? Integer.valueOf(0) : number;
	}
}
