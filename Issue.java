/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package library;

public class Issue {
    private int id, bookId, memberId, fine;
    private String issueDate, returnDate;

    public Issue(int id, int bookId, int memberId, String issueDate, String returnDate, int fine) {
        this.id = id;
        this.bookId = bookId;
        this.memberId = memberId;
        this.issueDate = issueDate;
        this.returnDate = returnDate;
        this.fine = fine;
    }

    public int getId() { return id; }
    public int getBookId() { return bookId; }
    public int getMemberId() { return memberId; }
    public String getIssueDate() { return issueDate; }
    public String getReturnDate() { return returnDate; }
    public int getFine() { return fine; }
}
