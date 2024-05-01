package net.leibi.transactions.datafetcher;

import com.netflix.graphql.dgs.*;
import lombok.RequiredArgsConstructor;
import net.leibi.books.generated.types.Account;
import net.leibi.books.generated.types.Transaction;
import net.leibi.transactions.service.TransactionDataService;

import java.util.List;
import java.util.Map;

@DgsComponent
@RequiredArgsConstructor
public class TransactionsDataFetcher {

    private final TransactionDataService transactionDataService;

    @DgsQuery
    public List<Transaction> transactions(@InputArgument Float minAmount) {
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
        return transactionDataService.getTransactionsByAccountId(account.getId());
    }

}
