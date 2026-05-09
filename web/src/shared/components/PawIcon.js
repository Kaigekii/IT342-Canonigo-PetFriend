/**
 * PawIcon Component - SVG Icon used throughout app
 */

export const PawIcon = ({ color = "#FFD8B9", size = 24 }) => (
  <svg
    width={size}
    height={size}
    viewBox="0 0 24 24"
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
  >
    <circle cx="12" cy="5" r="2.5" fill={color} />
    <circle cx="5" cy="12" r="2" fill={color} />
    <circle cx="19" cy="12" r="2" fill={color} />
    <circle cx="8" cy="18" r="2" fill={color} />
    <circle cx="16" cy="18" r="2" fill={color} />
    <path
      d="M12 7C13.66 7 15 8.34 15 10V14C15 16 13.5 18 12 19C10.5 18 9 16 9 14V10C9 8.34 10.34 7 12 7Z"
      fill={color}
      opacity="0.3"
    />
  </svg>
);

export default PawIcon;
