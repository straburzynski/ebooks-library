package pl.straburzynski.ebooks.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "BOOK")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private Integer year;

    @OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
    @JoinTable(
            name = "BOOK_AUTHOR",
            joinColumns = @JoinColumn(name = "BOOK_ID"),
            inverseJoinColumns = @JoinColumn(name = "AUTHOR_ID")
    )
    private List<Author> authors;

    @OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
    @JoinTable(
            name = "BOOK_CATEGORY",
            joinColumns = @JoinColumn(name = "BOOK_ID"),
            inverseJoinColumns = @JoinColumn(name = "CATEGORY_ID")
    )
    private List<Category> categories;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private Set<Format> formats;

}
