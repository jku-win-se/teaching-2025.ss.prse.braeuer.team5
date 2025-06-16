package jku.se;

/**
 * The {@code Status} enum represents the processing status of an invoice.
 * <p>
 * Possible values are:
 * <ul>
 *   <li>{@code APPROVED} – The invoice has been approved</li>
 *   <li>{@code PROCESSING} – The invoice is currently being processed</li>
 *   <li>{@code DECLINED} – The invoice has been declined</li>
 * </ul>
 */
public enum Status {
    APPROVED, PROCESSING, DECLINED
}
