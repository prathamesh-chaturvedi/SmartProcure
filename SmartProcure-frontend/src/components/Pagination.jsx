import React from 'react';

const Pagination = ({ page = 0, totalPages = 1, onPageChange }) => {
  if (totalPages <= 1) return null;

  const pages = [];
  const startPage = Math.max(0, page - 2);
  const endPage = Math.min(totalPages - 1, page + 2);

  for (let i = startPage; i <= endPage; i++) {
    pages.push(i);
  }

  return (
    <nav aria-label="Page navigation" className="d-flex justify-content-end mt-3">
      <ul className="pagination pagination-sm mb-0">
        <li className={`page-item ${page === 0 ? 'disabled' : ''}`}>
          <button
            className="page-item link-secondary page-link"
            onClick={() => onPageChange(page - 1)}
            disabled={page === 0}
          >
            <i className="bi bi-chevron-left"></i> Previous
          </button>
        </li>

        {startPage > 0 && (
          <>
            <li className="page-item">
              <button className="page-link" onClick={() => onPageChange(0)}>
                1
              </button>
            </li>
            {startPage > 1 && (
              <li className="page-item disabled">
                <span className="page-link">...</span>
              </li>
            )}
          </>
        )}

        {pages.map((p) => (
          <li key={p} className={`page-item ${p === page ? 'active' : ''}`}>
            <button
              className={`page-link ${p === page ? 'btn-sp-primary border-0' : ''}`}
              onClick={() => onPageChange(p)}
            >
              {p + 1}
            </button>
          </li>
        ))}

        {endPage < totalPages - 1 && (
          <>
            {endPage < totalPages - 2 && (
              <li className="page-item disabled">
                <span className="page-link">...</span>
              </li>
            )}
            <li className="page-item">
              <button className="page-link" onClick={() => onPageChange(totalPages - 1)}>
                {totalPages}
              </button>
            </li>
          </>
        )}

        <li className={`page-item ${page >= totalPages - 1 ? 'disabled' : ''}`}>
          <button
            className="page-link link-secondary"
            onClick={() => onPageChange(page + 1)}
            disabled={page >= totalPages - 1}
          >
            Next <i className="bi bi-chevron-right"></i>
          </button>
        </li>
      </ul>
    </nav>
  );
};

export default Pagination;
