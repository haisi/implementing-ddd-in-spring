package li.selman.ddd.statement;

import java.time.LocalDate;

public class StatementFixture {

  public static Builder aStatement() {
    return new Builder();
  }

  public static class Builder {

    private LocalDate date = LocalDate.of(2022, 12, 4);

    private Statement.Type type = Statement.Type.HELPFUL;

    private Integer sequenceOfDay = 1;

    public Builder of(Statement.Type type) {
      this.type = type;
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
      var id = new Statement.StatementId(date, type, sequenceOfDay);
      return new Statement(id);
    }
  }
}
