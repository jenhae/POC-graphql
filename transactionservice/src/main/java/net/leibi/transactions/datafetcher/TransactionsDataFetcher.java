package net.leibi.transactions.datafetcher;

import com.netflix.graphql.dgs.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.leibi.transactions.generated.types.Account;
import net.leibi.transactions.generated.types.Transaction;
import net.leibi.transactions.service.TransactionDataService;

import java.util.List;
import java.util.Map;

@DgsComponent
@RequiredArgsConstructor
@Log4j2
public class TransactionsDataFetcher {

    private final TransactionDataService transactionDataService;

    @DgsQuery
    public List<Transaction> transactions(@InputArgument Double minAmount) {
        if (minAmount == null) {
            return transactionDataService.getData();
        }

        return transactionDataService.getTransactionsByMinAmount(minAmount);
    }

    @DgsQuery
    public Transaction transactionById(@InputArgument String id) {
        return transactionDataService.getTransactionById(id);
    }

    @DgsEntityFetcher(name = "Account")
    public Account account(Map<String, Object> values) {
        return new Account((String) values.get("id"), null);
    }

    @DgsData(parentType = "Account", field = "transactions")
    public List<Transaction> transactionFetcher(DgsDataFetchingEnvironment dataFetchingEnvironment) {
        Account account = dataFetchingEnvironment.getSource();
        log.info("get transactions for account: {}", account);
        return transactionDataService.getTransactionsByAccountId(account.getId());
    }

}