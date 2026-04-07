package com.fuelstation.model.dto.response;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Envelope de paginação retornado nas listagens.
 *
 * <p>Fornece todos os metadados necessários para que o frontend (React)
 * renderize controles de paginação sem precisar inferir nada.</p>
 *
 * <pre>
 * {
 *   "content":       [...],
 *   "page":          0,
 *   "size":          20,
 *   "totalElements": 100,
 *   "totalPages":    5,
 *   "first":         true,
 *   "last":          false
 * }
 * </pre>
 *
 * @param <T> tipo do item contido na página
 */
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) {

    /**
     * Cria um {@code PageResponse} a partir de um {@link Page} do Spring Data.
     *
     * @param springPage página retornada pelo repositório
     * @param <T>        tipo do item
     * @return envelope pronto para serialização JSON
     */
    public static <T> PageResponse<T> from(Page<T> springPage) {
        return new PageResponse<>(
                springPage.getContent(),
                springPage.getNumber(),
                springPage.getSize(),
                springPage.getTotalElements(),
                springPage.getTotalPages(),
                springPage.isFirst(),
                springPage.isLast()
        );
    }
}

