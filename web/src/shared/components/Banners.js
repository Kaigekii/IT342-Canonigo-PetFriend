"use client";

/**
 * Loading Spinner Component
 */

export const LoadingSpinner = ({ message = "Loading..." }) => {
  return (
    <div
      style={{
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        justifyContent: "center",
        minHeight: "200px",
        gap: "16px",
      }}
    >
      <div
        style={{
          border: "4px solid #F0F0F0",
          borderTop: "4px solid #FFD8B9",
          borderRadius: "50%",
          width: "40px",
          height: "40px",
          animation: "spin 1s linear infinite",
        }}
      />
      <p style={{ color: "#333333" }}>{message}</p>
      <style>{`
        @keyframes spin {
          0% { transform: rotate(0deg); }
          100% { transform: rotate(360deg); }
        }
      `}</style>
    </div>
  );
};

/**
 * Error Banner Component
 */

export const ErrorBanner = ({ message, onClose }) => {
  return (
    <div
      style={{
        backgroundColor: "#FFCCBC",
        border: "1px solid #FFB6C1",
        borderRadius: "8px",
        padding: "16px",
        marginBottom: "16px",
        display: "flex",
        justifyContent: "space-between",
        alignItems: "center",
      }}
    >
      <p style={{ color: "#333333", margin: 0 }}>❌ {message}</p>
      {onClose && (
        <button
          onClick={onClose}
          style={{
            background: "none",
            border: "none",
            cursor: "pointer",
            fontSize: "18px",
            color: "#333333",
          }}
        >
          ✕
        </button>
      )}
    </div>
  );
};

/**
 * Success Banner Component
 */

export const SuccessBanner = ({ message, onClose }) => {
  return (
    <div
      style={{
        backgroundColor: "#B6E5D8",
        border: "1px solid #A0D9CC",
        borderRadius: "8px",
        padding: "16px",
        marginBottom: "16px",
        display: "flex",
        justifyContent: "space-between",
        alignItems: "center",
      }}
    >
      <p style={{ color: "#333333", margin: 0 }}>✅ {message}</p>
      {onClose && (
        <button
          onClick={onClose}
          style={{
            background: "none",
            border: "none",
            cursor: "pointer",
            fontSize: "18px",
            color: "#333333",
          }}
        >
          ✕
        </button>
      )}
    </div>
  );
};

/**
 * Warning Banner Component
 */

export const WarningBanner = ({ message, onClose }) => {
  return (
    <div
      style={{
        backgroundColor: "#FFF9C4",
        border: "1px solid #FFE082",
        borderRadius: "8px",
        padding: "16px",
        marginBottom: "16px",
        display: "flex",
        justifyContent: "space-between",
        alignItems: "center",
      }}
    >
      <p style={{ color: "#333333", margin: 0 }}>⚠️ {message}</p>
      {onClose && (
        <button
          onClick={onClose}
          style={{
            background: "none",
            border: "none",
            cursor: "pointer",
            fontSize: "18px",
            color: "#333333",
          }}
        >
          ✕
        </button>
      )}
    </div>
  );
};

export default {
  LoadingSpinner,
  ErrorBanner,
  SuccessBanner,
  WarningBanner,
};
