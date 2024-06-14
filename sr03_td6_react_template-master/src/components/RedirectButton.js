import React from 'react';

const RedirectButton = ({ url, children }) => {
    const handleRedirect = () => {
        window.location.href = url;
    };

    return (
        <button onClick={handleRedirect} className="redirect-button">
            {children}
        </button>
    );
};

export default RedirectButton;
