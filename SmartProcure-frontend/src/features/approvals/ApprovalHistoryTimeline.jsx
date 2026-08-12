import React from 'react';

const ApprovalHistoryTimeline = ({ history = [] }) => {
  if (history.length === 0) {
    return (
      <div className="text-center py-4 text-muted bg-light rounded border border-dashed">
        <i className="bi bi-clock-history fs-2 d-block mb-1 text-secondary"></i>
        No approval history recorded yet.
      </div>
    );
  }

  // Group records by approvalCycle
  const cycles = history.reduce((acc, item) => {
    const cycleNum = item.approvalCycle || 1;
    if (!acc[cycleNum]) acc[cycleNum] = [];
    acc[cycleNum].push(item);
    return acc;
  }, {});

  const cycleKeys = Object.keys(cycles).sort((a, b) => Number(b) - Number(a)); // latest cycle first

  const getActionBadge = (action) => {
    switch (action) {
      case 'APPROVED':
        return <span className="badge bg-success"><i className="bi bi-check-circle me-1"></i>Approved</span>;
      case 'REJECTED':
        return <span className="badge bg-danger"><i className="bi bi-x-circle me-1"></i>Rejected</span>;
      case 'PENDING':
        return <span className="badge bg-warning text-dark"><i className="bi bi-hourglass-split me-1"></i>Pending</span>;
      default:
        return <span className="badge bg-secondary">{action}</span>;
    }
  };

  return (
    <div className="sp-timeline">
      {cycleKeys.map((cycleNum) => (
        <div key={cycleNum} className="mb-4">
          <div className="d-flex align-items-center mb-3">
            <span className="badge bg-dark px-3 py-1.5 font-monospace fs-6 me-2">
              Approval Cycle {cycleNum}
            </span>
            <hr className="flex-grow-1 my-0 opacity-25" />
          </div>

          <div className="list-group">
            {cycles[cycleNum].map((record, index) => (
              <div
                key={index}
                className="list-group-item p-3 border-start border-3"
                style={{
                  borderLeftColor:
                    record.action === 'APPROVED'
                      ? '#16a34a'
                      : record.action === 'REJECTED'
                      ? '#dc2626'
                      : '#ca8a04',
                }}
              >
                <div className="d-flex justify-content-between align-items-start mb-1">
                  <div>
                    <span className="fw-bold text-slate-800 me-2">
                      Level {record.approvalLevel}: {record.approverName || 'Assigned Approver'}
                    </span>
                  </div>
                  <div>{getActionBadge(record.action)}</div>
                </div>

                {record.remarks ? (
                  <div className="small text-secondary bg-light p-2 rounded mt-2 border">
                    <i className="bi bi-chat-left-quote me-1"></i> "{record.remarks}"
                  </div>
                ) : (
                  <div className="small text-muted fst-italic mt-1">No remarks provided.</div>
                )}
              </div>
            ))}
          </div>
        </div>
      ))}
    </div>
  );
};

export default ApprovalHistoryTimeline;
