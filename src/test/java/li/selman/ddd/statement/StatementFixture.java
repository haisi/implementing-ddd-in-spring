package li.selman.ddd.statement;

import java.time.LocalDate;
import li.selman.ddd.statement.Statement.StatementId;

public class StatementFixture {

  public static final StatementId STATEMENT_ID =
      new StatementId(LocalDate.of(2022, 12, 4), Statement.Source.YOUTUBE, 2);

  public static Builder aStatement() {
    return new Builder();
  }

  public static class Builder {

    private LocalDate date = LocalDate.of(2022, 12, 4);

    private Statement.Source source = Statement.Source.TWITTER;

    private Integer sequenceOfDay = 1;

    public Builder of(Statement.Source source) {
      this.source = source;
      return this;
    }

    public Builder at(LocalDate date) {
      this.date = date;
      return this;
    }

    public Builder setSequenceOfDay(Integer sequenceOfDay) {
      this.sequenceOfDay = sequenceOfDay;
      return this;
    }

    public Statement build() {
      var id = new StatementId(date, source, sequenceOfDay);
      return new Statement(id);
    }
  }
}
